package com.finpro.twogoods.service;

import com.finpro.twogoods.client.MidtransFeignClient;
import com.finpro.twogoods.client.dto.MidtransSnapRequest;
import com.finpro.twogoods.client.dto.MidtransSnapResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MidtransService {

	private final MidtransFeignClient midtransFeignClient;

	@Value("${midtrans.api-key}")
	private String serverKey;

	@Transactional(rollbackFor = Exception.class)
	public MidtransSnapResponse createSnap(MidtransSnapRequest request) {
		System.out.println(">>> createSnap called");
		return midtransFeignClient.createTransaction(request);
	}

	public boolean isValidSignature(String orderId, String statusCode, String grossAmount, String signatureKey) {
		try {
			String raw = orderId + statusCode + grossAmount + serverKey;
			MessageDigest md = MessageDigest.getInstance("SHA-512");
			byte[] hash = md.digest(raw.getBytes(StandardCharsets.UTF_8));

			StringBuilder hex = new StringBuilder();
			for (byte b : hash) hex.append(String.format("%02x", b));

			return hex.toString().equals(signatureKey);
		} catch (Exception e) {
			return false;
		}
	}
}
