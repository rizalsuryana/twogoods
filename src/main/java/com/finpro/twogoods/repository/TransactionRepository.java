package com.finpro.twogoods.repository;

import com.finpro.twogoods.entity.Transaction;
import com.finpro.twogoods.entity.MerchantProfile;
import com.finpro.twogoods.entity.User;
import com.finpro.twogoods.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
	List<Transaction> findByCustomer(User customer);
	List<Transaction> findByMerchant(MerchantProfile merchant);
	Optional<Transaction> findByOrderId(String orderId);

	List<Transaction> findAllByOrderId(String orderId);
	List<Transaction> findByStatusAndCreatedAtBefore(OrderStatus status, LocalDateTime time);

}