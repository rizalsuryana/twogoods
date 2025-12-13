package com.finpro.twogoods.service;

import com.finpro.twogoods.client.dto.MidtransSnapRequest;
import com.finpro.twogoods.client.dto.MidtransSnapResponse;
import com.finpro.twogoods.dto.response.CheckoutResponse;
import com.finpro.twogoods.dto.response.TransactionResponse;
import com.finpro.twogoods.entity.*;
import com.finpro.twogoods.enums.OrderStatus;
import com.finpro.twogoods.exceptions.ApiException;
import com.finpro.twogoods.repository.CartItemRepository;
import com.finpro.twogoods.repository.ProductRepository;
import com.finpro.twogoods.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CheckoutService {

	private final CartItemRepository cartItemRepository;
	private final TransactionRepository transactionRepository;
	private final ProductRepository productRepository;
	private final MidtransService midtransService;

	@Transactional(rollbackFor = Exception.class)
	public CheckoutResponse checkout(List<Long> cartItemIds) {
		User user = getCurrentUser();

		if (cartItemIds == null || cartItemIds.isEmpty()) {
			throw new ApiException("No cart items selected");
		}

		// Ambil cart item berdasarkan ID yang dikirim FE
		List<CartItem> items = cartItemRepository.findByIdIn(cartItemIds)
				.stream()
				.filter(i -> i.getUser().getId().equals(user.getId()))
				.toList();

		if (items.isEmpty()) {
			throw new ApiException("No valid cart items found for this user");
		}

		List<TransactionResponse> responses = new ArrayList<>();
		int total = 0;

		for (CartItem cart : items) {
			Product product = cart.getProduct();

			// Block product yang sudah tidak available
			if (!product.getIsAvailable()) {
				throw new ApiException("Product " + product.getName() + " is sold out");
			}

			Transaction trx = Transaction.builder()
					.customer(user)
					.merchant(cart.getMerchant())
					.status(OrderStatus.PENDING)
					.totalPrice(product.getPrice())
					.build();

			TransactionItem item = TransactionItem.builder()
					.transaction(trx)
					.product(product)
					.price(product.getPrice())
					.quantity(1)
					.build();

			// pastikan list items tidak null
			trx.getItems().add(item);

			Transaction savedTransaction = transactionRepository.save(trx);

			total += item.getPrice().intValue();

			// Mark product sebagai sold out (kalau memang 1 quantity)
			product.setIsAvailable(false);
			productRepository.save(product);

			responses.add(savedTransaction.toResponse());
		}

		// Hapus hanya cart item yang di-checkout
		cartItemRepository.deleteAll(items);

		// Generate unique orderId buat Midtrans
		String orderId = "ORDER-" + user.getId() + "-" + UUID.randomUUID();

		MidtransSnapRequest request = MidtransSnapRequest.builder()
				.transactionDetails(
						MidtransSnapRequest.TransactionDetails.builder()
								.orderId(orderId)
								.grossAmount(total)
								.build()
				)
				.callbacks(new MidtransSnapRequest.Callbacks("https://www.2goods.com"))
				.build();

		MidtransSnapResponse midtransResponse = midtransService.createSnap(request);

		return CheckoutResponse.builder()
				.midtransSnap(midtransResponse)
				.transactions(responses)
				.build();
	}

	private User getCurrentUser() {
		return (User) SecurityContextHolder.getContext()
				.getAuthentication()
				.getPrincipal();
	}
}
