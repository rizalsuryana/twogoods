package com.finpro.twogoods.client.dto;

import com.finpro.twogoods.entity.TransactionItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MidtransSnapRequest {
	private TransactionDetails transaction_details;
	private CreditCard credit_card;
	private List<TransactionItem> item_details;
	private String customerId;

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class TransactionDetails {
		private String order_id;
		private Integer gross_amount;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CreditCard {
		private Boolean secure;
	}
}
