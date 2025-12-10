package com.finpro.twogoods.controller;

import com.finpro.twogoods.entity.MerchantProfile;
import com.finpro.twogoods.entity.User;
import com.finpro.twogoods.service.MerchantProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/merchant-profiles")
@RequiredArgsConstructor
public class MerchantProfileController {

	private final MerchantProfileService merchantProfileService;

	@GetMapping
	public ResponseEntity<Page<MerchantProfile>> getAllPaginated(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size
	) {
		Page<MerchantProfile> profiles =
				merchantProfileService.getAllPaginated(PageRequest.of(page, size));

		return ResponseEntity.ok(profiles);
	}

	@GetMapping("/all")
	public ResponseEntity<List<MerchantProfile>> getAll() {
		return ResponseEntity.ok(merchantProfileService.getAllMerchantProfiles());
	}

	@GetMapping("/{id}")
	public ResponseEntity<MerchantProfile> getById(@PathVariable Long id) {
		return ResponseEntity.ok(merchantProfileService.getMerchantById(id));
	}

	@PutMapping("/{id}")
	public ResponseEntity<MerchantProfile> update(
			@PathVariable Long id,
			@RequestBody MerchantProfile merchantProfile,
			Authentication auth
	) {
		User user = (User) auth.getPrincipal();

		// hanya MERCHANT yang boleh update merchant profile
		if (!user.getRole().name().equals("MERCHANT")) {
			throw new AccessDeniedException("Only MERCHANT can update merchant profile");
		}

		// hanya boleh update profile miliknya sendiri
		if (!user.getId().equals(id)) {
			throw new AccessDeniedException("You can only update your own merchant profile");
		}

		return ResponseEntity.ok(
				merchantProfileService.updateMerchantProfile(id, merchantProfile)
		);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
		User user = (User) auth.getPrincipal();

		// hanya MERCHANT yang boleh delete merchant profile
		if (!user.getRole().name().equals("MERCHANT")) {
			throw new AccessDeniedException("Only MERCHANT can delete merchant profile");
		}

		// hanya boleh delete profile miliknya sendiri
		if (!user.getId().equals(id)) {
			throw new AccessDeniedException("You can only delete your own merchant profile");
		}

		merchantProfileService.deleteMerchantProfileById(id);
		return ResponseEntity.noContent().build();
	}
}
