package com.finpro.twogoods.controller;

import com.finpro.twogoods.dto.response.ApiResponse;
import com.finpro.twogoods.dto.response.UserResponse;
import com.finpro.twogoods.entity.User;
import com.finpro.twogoods.service.UserService;
import com.finpro.twogoods.utils.ResponseUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/me")
	public ResponseEntity<?> getMe() {
		UserResponse response = userService.getMe();
		return ResponseUtil.buildSingleResponse(HttpStatus.OK, "OK", response);
	}

	@GetMapping
	public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(required = false) String role,
			@RequestParam(required = false) String search
	) {
		return ResponseEntity.ok(userService.getAllUsers(page, size, role, search));
	}

	@PutMapping("/profile-picture")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<?> uploadProfilePicture(
			@RequestParam("file") MultipartFile file
	) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = (User) authentication.getPrincipal();

		User updated = userService.updateProfilePicture(user.getId(), file);

		return ResponseEntity.ok(updated.toResponse());
	}
}
