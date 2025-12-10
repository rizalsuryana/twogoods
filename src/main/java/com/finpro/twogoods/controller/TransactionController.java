package com.finpro.twogoods.controller;

import com.finpro.twogoods.dto.request.CreateTransactionRequest;
import com.finpro.twogoods.dto.response.ApiResponse;
import com.finpro.twogoods.dto.response.TransactionResponse;
import com.finpro.twogoods.enums.OrderStatus;
import com.finpro.twogoods.service.TransactionService;
import com.finpro.twogoods.utils.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

	private final TransactionService transactionService;

	@PostMapping
	public ResponseEntity<ApiResponse<TransactionResponse>> create(
			@RequestBody CreateTransactionRequest request
	) {
		return ResponseUtil.buildSingleResponse(
				HttpStatus.CREATED,
				"Transaction created successfully",
				transactionService.createTransaction(request)
		);
	}

	@GetMapping("/me")
	public ResponseEntity<ApiResponse<List<TransactionResponse>>> myTransactions() {
		return ResponseUtil.buildSingleResponse(
				HttpStatus.OK,
				"My transactions fetched successfully",
				transactionService.getMyTransactions()
		);
	}

	@GetMapping("/merchant")
	public ResponseEntity<ApiResponse<List<TransactionResponse>>> merchantOrders() {
		return ResponseUtil.buildSingleResponse(
				HttpStatus.OK,
				"Merchant orders fetched successfully",
				transactionService.getMerchantOrders()
		);
	}

	@PutMapping("/{id}/status")
	public ResponseEntity<ApiResponse<TransactionResponse>> updateStatus(
			@PathVariable Long id,
			@RequestParam OrderStatus status
	) {
		return ResponseUtil.buildSingleResponse(
				HttpStatus.OK,
				"Transaction status updated successfully",
				transactionService.updateStatus(id, status)
		);
	}
}
