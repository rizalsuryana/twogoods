package com.finpro.twogoods.controller;

import com.finpro.twogoods.dto.request.UserRequest;
import com.finpro.twogoods.dto.response.ApiResponse;
import com.finpro.twogoods.dto.response.StatusResponse;
import com.finpro.twogoods.dto.response.UserResponse;
import com.finpro.twogoods.entity.User;
import com.finpro.twogoods.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@GetMapping
	public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(required = false) String role,
			@RequestParam(required = false) String search
	) {
		ApiResponse<List<UserResponse>> response = userService.getAllUsers(page, size, role, search);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/me")
	public ResponseEntity<ApiResponse<UserResponse>> getMe() {
		UserResponse me = userService.getMe();
		return ResponseEntity.ok(
				ApiResponse.<UserResponse>builder()
						.status(new StatusResponse(200, "Current user fetched successfully"))
						.data(me)
						.build()
		);
	}

	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<UserResponse>> updateUser(
			@PathVariable Long id,
			@RequestBody UserRequest request,
			Authentication auth
	) {
		User user = (User) auth.getPrincipal();

		// User hanya boleh update dirinya sendiri
		if (!user.getId().equals(id)) {
			throw new AccessDeniedException("You can only update your own account");
		}

		User updated = userService.updateUser(id, request);

		return ResponseEntity.ok(
				ApiResponse.<UserResponse>builder()
						.status(new StatusResponse(200, "User updated successfully"))
						.data(updated.toResponse())
						.build()
		);
	}

	@PutMapping("/{id}/profile-picture")
	public ResponseEntity<ApiResponse<UserResponse>> updateProfilePicture(
			@PathVariable Long id,
			@RequestParam("file") MultipartFile file,
			Authentication auth
	) {
		User user = (User) auth.getPrincipal();

		// User hanya boleh update foto dirinya sendiri
		if (!user.getId().equals(id)) {
			throw new AccessDeniedException("You can only update your own profile picture");
		}

		User updated = userService.updateProfilePicture(id, file);

		return ResponseEntity.ok(
				ApiResponse.<UserResponse>builder()
						.status(new StatusResponse(200, "Profile picture updated successfully"))
						.data(updated.toResponse())
						.build()
		);
	}
}
