package com.finpro.twogoods.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "transaction_items")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class TransactionItem extends BaseEntity {

	@ManyToOne
	@JoinColumn(name = "transaction_id")
	private Transaction transaction;

	@ManyToOne
	@JoinColumn(name = "product_id")
	private Product product;

	private BigDecimal price;
}
