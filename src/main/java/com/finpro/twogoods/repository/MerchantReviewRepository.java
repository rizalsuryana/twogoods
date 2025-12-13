package com.finpro.twogoods.repository;

import com.finpro.twogoods.entity.MerchantReview;
import com.finpro.twogoods.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MerchantReviewRepository extends JpaRepository<MerchantReview, Long> {

	boolean existsByTransaction(Transaction transaction);

<<<<<<< HEAD
	@Query("SELECT AVG(r.rating) FROM MerchantReview r WHERE r.merchant.id = :merchantId")
	Double getAverageRating(Long merchantId);
=======
	@Query("SELECT COALESCE(AVG(r.rating), 0) FROM MerchantReview r WHERE r.merchant.id = :merchantId")
	Float getAverageRating(Long merchantId);

>>>>>>> 71535bb1b83178b2662cd9debb5c599b1f32f656

	@Query("SELECT COUNT(r) FROM MerchantReview r WHERE r.merchant.id = :merchantId")
	Long getTotalReviews(Long merchantId);
}
