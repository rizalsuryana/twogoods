package com.finpro.twogoods.controller;

import com.finpro.twogoods.dto.request.CreateTransactionRequest;
import com.finpro.twogoods.dto.response.TransactionResponse;
import com.finpro.twogoods.enums.OrderStatus;
import com.finpro.twogoods.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

	private final TransactionService transactionService;

	@PostMapping
	public ResponseEntity<TransactionResponse> create(@RequestBody CreateTransactionRequest request) {
		return ResponseEntity.ok(transactionService.createTransaction(request));
	}

	@GetMapping("/me")
	public ResponseEntity<List<TransactionResponse>> myTransactions() {
		return ResponseEntity.ok(transactionService.getMyTransactions());
	}

	@GetMapping("/merchant")
	public ResponseEntity<List<TransactionResponse>> merchantOrders() {
		return ResponseEntity.ok(transactionService.getMerchantOrders());
	}

	@PutMapping("/{id}/status")
	public ResponseEntity<TransactionResponse> updateStatus(
			@PathVariable Long id,
			@RequestParam OrderStatus status
	) {
		return ResponseEntity.ok(transactionService.updateStatus(id, status));
	}
}
