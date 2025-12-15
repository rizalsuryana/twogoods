package com.finpro.twogoods.controller;

import com.finpro.twogoods.client.dto.MidtransNotification;
import com.finpro.twogoods.client.dto.MidtransRefundResponse;
import com.finpro.twogoods.client.dto.MidtransSnapRequest;
import com.finpro.twogoods.client.dto.MidtransSnapResponse;
import com.finpro.twogoods.dto.response.ApiResponse;
import com.finpro.twogoods.dto.response.TransactionResponse;
import com.finpro.twogoods.service.MidtransService;
import com.finpro.twogoods.utils.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/midtrans")
@RequiredArgsConstructor
public class MidtransController {

	private final MidtransService midtransService;

	@Operation(
			summary = "Test snap request",
			description = "Testing request for snap."
	)
	@PostMapping("/snap")
	private ResponseEntity<ApiResponse<MidtransSnapResponse>> snap(@RequestBody MidtransSnapRequest request){
		return ResponseUtil.buildSingleResponse(
				HttpStatus.OK,
				HttpStatus.OK.getReasonPhrase(),
				midtransService.createSnap(request)
											   );
	}

	@PostMapping("/notification")
	public ResponseEntity<ApiResponse<TransactionResponse>> webhook(@RequestBody MidtransNotification notif) {

		boolean valid = midtransService.isValidSignature(
				notif.getOrderId(),
				notif.getStatusCode(),
				notif.getGrossAmount(),
				notif.getSignatureKey()
												  );

		if (!valid) {
			return ResponseUtil.buildSingleResponse(
					HttpStatus.UNAUTHORIZED,
					HttpStatus.UNAUTHORIZED.getReasonPhrase(),
					null
												   );
		}

		TransactionResponse trx = midtransService.updateStatus(notif);

		return ResponseUtil.buildSingleResponse(
				HttpStatus.OK,
				"Successfully Updated Status",
				trx
											   );
	}

	@PostMapping("/refund/{orderId}")
	public MidtransRefundResponse refund(
			@PathVariable String orderId,
			@RequestParam int amount
										) {
		return midtransService.refund(orderId, amount);
	}

	@PostMapping("/direct-refund/{orderId}")
	public MidtransRefundResponse directRefund(
			@PathVariable String orderId,
			@RequestParam int amount
											  ) {
		return midtransService.directRefund(orderId, amount);
	}


}
