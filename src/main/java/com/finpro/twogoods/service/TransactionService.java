package com.finpro.twogoods.service;

import com.finpro.twogoods.dto.request.CreateTransactionRequest;
import com.finpro.twogoods.dto.response.TransactionResponse;
import com.finpro.twogoods.entity.*;
import com.finpro.twogoods.enums.OrderStatus;
import com.finpro.twogoods.enums.UserRole;
import com.finpro.twogoods.exceptions.ApiException;
import com.finpro.twogoods.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

	private final TransactionRepository transactionRepository;
	private final TransactionItemRepository transactionItemRepository;
	private final ProductRepository productRepository;
	private final MerchantProfileRepository merchantProfileRepository;

	@Transactional(rollbackFor = Exception.class)
	public TransactionResponse createTransaction(CreateTransactionRequest request) {

		User currentUser = (User) SecurityContextHolder.getContext()
				.getAuthentication()
				.getPrincipal();

		if (!currentUser.getRole().equals(UserRole.CUSTOMER)) {
			throw new ApiException("Only customers can create transactions");
		}

		Product product = productRepository.findById(request.getProductId())
				.orElseThrow(() -> new ApiException("Product not found"));

		MerchantProfile merchant = product.getMerchant();

		if (merchant.getUser().getId().equals(currentUser.getId())) {
			throw new ApiException("Merchant cannot buy their own product");
		}

		Transaction transaction = Transaction.builder()
				.customer(currentUser)
				.merchant(merchant)
				.status(OrderStatus.PENDING)
				.totalPrice(product.getPrice())
				.build();

		Transaction savedTransaction = transactionRepository.save(transaction);

		TransactionItem item = TransactionItem.builder()
				.transaction(savedTransaction)
				.product(product)
				.price(product.getPrice())
				.build();

		transactionItemRepository.save(item);

		return savedTransaction.toResponse();
	}

	@Transactional(readOnly = true)
	public List<TransactionResponse> getMyTransactions() {
		User customer = (User) SecurityContextHolder.getContext()
				.getAuthentication()
				.getPrincipal();

		return transactionRepository.findByCustomer(customer)
				.stream()
				.map(Transaction::toResponse)
				.toList();
	}

	@Transactional(readOnly = true)
	public List<TransactionResponse> getMerchantOrders() {
		User merchantUser = (User) SecurityContextHolder.getContext()
				.getAuthentication()
				.getPrincipal();

		MerchantProfile merchant = merchantProfileRepository.findByUser(merchantUser)
				.orElseThrow(() -> new ApiException("Merchant profile not found"));

		return transactionRepository.findByMerchant(merchant)
				.stream()
				.map(Transaction::toResponse)
				.toList();
	}

	@Transactional(rollbackFor = Exception.class)
	public TransactionResponse updateStatus(Long id, OrderStatus newStatus) {

		User currentUser = (User) SecurityContextHolder.getContext()
				.getAuthentication()
				.getPrincipal();

		Transaction trx = transactionRepository.findById(id)
				.orElseThrow(() -> new ApiException("Transaction not found"));

		boolean isCustomer = trx.getCustomer().getId().equals(currentUser.getId());
		boolean isMerchant = trx.getMerchant().getUser().getId().equals(currentUser.getId());

		if (!isCustomer && !isMerchant) {
			throw new ApiException("You are not allowed to update this transaction");
		}

		OrderStatus currentStatus = trx.getStatus();

		switch (newStatus) {

			case PAID:
				if (!isMerchant) throw new ApiException("Only merchant can set PAID");
				if (currentStatus != OrderStatus.PENDING)
					throw new ApiException("PAID can only be set from PENDING");
				break;

			case SHIPPED:
				if (!isMerchant) throw new ApiException("Only merchant can set SHIPPED");
				if (currentStatus != OrderStatus.PAID)
					throw new ApiException("SHIPPED can only be set from PAID");
				break;

			case COMPLETED:
				if (!isCustomer) throw new ApiException("Only customer can set COMPLETED");
				if (currentStatus != OrderStatus.SHIPPED)
					throw new ApiException("COMPLETED can only be set from SHIPPED");
				break;

			case CANCELED:
				if (currentStatus == OrderStatus.SHIPPED || currentStatus == OrderStatus.COMPLETED) {
					throw new ApiException("Cannot cancel after item is shipped");
				}
				break;

			default:
				throw new ApiException("Invalid status update");
		}

		trx.setStatus(newStatus);
		Transaction updated = transactionRepository.save(trx);

		return updated.toResponse();
	}
}
