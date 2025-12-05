package com.finpro.twogoods.controller;

import com.finpro.twogoods.entity.Customer;
import com.finpro.twogoods.entity.UserRole;
import com.finpro.twogoods.model.request.LoginRequest;
import com.finpro.twogoods.model.response.LoginResponse;
import com.finpro.twogoods.service.AuthService;
import com.finpro.twogoods.utils.ResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping( "/api/v1/auth" )
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@PostMapping( "/login" )
	public ResponseEntity<?> loginHandler(@RequestBody @Valid LoginRequest request) {

		LoginResponse response = authService.login(request);

		return ResponseUtil.buildSingleResponse(HttpStatus.OK, HttpStatus.OK.getReasonPhrase(), response);
	}

}
