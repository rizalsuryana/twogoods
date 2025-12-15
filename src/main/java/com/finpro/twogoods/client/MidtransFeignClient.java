package com.finpro.twogoods.client;

import com.finpro.twogoods.client.dto.MidtransRefundRequest;
import com.finpro.twogoods.client.dto.MidtransRefundResponse;
import com.finpro.twogoods.client.dto.MidtransSnapRequest;
import com.finpro.twogoods.client.dto.MidtransSnapResponse;
import com.finpro.twogoods.config.MidtransFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
		name = "midtransClient",
		url = "https://app.sandbox.midtrans.com",
		configuration = MidtransFeignConfig.class
)
public interface MidtransFeignClient {

	@PostMapping(
			value = "/snap/v1/transactions",
			consumes = "application/json",
			produces = "application/json"
	)
	MidtransSnapResponse createTransaction(@RequestBody MidtransSnapRequest body);

	@PostMapping(
			value = "/v2/{orderId}/refund",
			consumes = "application/json",
			produces = "application/json"
	)
	MidtransRefundResponse refund(
			@PathVariable("orderId") String orderId,
			@RequestBody MidtransRefundRequest body
								 );

	@PostMapping(
			value = "/v2/{orderId}/direct_refund",
			consumes = "application/json",
			produces = "application/json"
	)
	MidtransRefundResponse directRefund(
			@PathVariable("orderId") String orderId,
			@RequestBody MidtransRefundRequest body
									   );
}
