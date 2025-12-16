package com.finpro.twogoods.utils.shedule;

import com.finpro.twogoods.enums.OrderStatus;
import com.finpro.twogoods.repository.ProductRepository;
import com.finpro.twogoods.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class PendingAutoCancelScheduler {

	private final TransactionRepository transactionRepository;
	private final ProductRepository productRepository;

	@Scheduled(fixedRate = 60 * 1000) // tiap 1 menit
	@Transactional(rollbackFor = Exception.class)
	public void autoCancelPending() {
// 30 min
//		LocalDateTime limit = LocalDateTime.now().minusMinutes(30);
//		1 min
		LocalDateTime limit = LocalDateTime.now().minusMinutes(1);

		transactionRepository
				.findByStatusAndCreatedAtBefore(OrderStatus.PENDING, limit)
				.forEach(trx -> {

					trx.setStatus(OrderStatus.CANCELED);
					trx.setCustomerCancelRequest(false);
					trx.setMerchantCancelConfirm(false);

					trx.getItems().forEach(item -> {
						item.getProduct().setIsAvailable(true);
						productRepository.save(item.getProduct());
					});

					transactionRepository.save(trx);
				});
	}
}

