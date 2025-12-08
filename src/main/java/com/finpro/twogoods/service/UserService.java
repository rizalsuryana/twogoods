package com.finpro.twogoods.service;

import com.finpro.twogoods.dto.request.CustomerRegisterRequest;
import com.finpro.twogoods.dto.request.MerchantRegisterRequest;
import com.finpro.twogoods.dto.request.UserRequest;
import com.finpro.twogoods.dto.response.ApiResponse;
import com.finpro.twogoods.dto.response.PagingResponse;
import com.finpro.twogoods.dto.response.StatusResponse;
import com.finpro.twogoods.dto.response.UserResponse;
import com.finpro.twogoods.entity.CustomerProfile;
import com.finpro.twogoods.entity.MerchantProfile;
import com.finpro.twogoods.entity.User;
import com.finpro.twogoods.enums.UserRole;
import com.finpro.twogoods.exceptions.ResourceDuplicateException;
import com.finpro.twogoods.exceptions.ResourceNotFoundException;
import com.finpro.twogoods.repository.CustomerProfileRepository;
import com.finpro.twogoods.repository.MerchantProfileRepository;
import com.finpro.twogoods.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService implements UserDetailsService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final CustomerProfileRepository customerProfileRepository;
	private final MerchantProfileRepository merchantProfileRepository;
	private final CloudinaryService cloudinaryService;

//auth spring

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		log.debug("Loading user by email: {}", email);
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("Email or password is incorrect"));
	}


	@Transactional(rollbackFor = Exception.class)
	public User createCustomer(CustomerRegisterRequest request) {

		String username = validateUserOnRegister(
				request.getPassword(),
				request.getConfirmPassword(),
				request.getEmail()
		);

		User user = User.builder()
				.username(username)
				.email(request.getEmail())
				.password(passwordEncoder.encode(request.getPassword()))
				.fullName(request.getFullName())
				.role(UserRole.CUSTOMER)
				.enabled(true)
				.build();

		userRepository.save(user);

		CustomerProfile profile = CustomerProfile.builder()
				.user(user)
				.build();

		customerProfileRepository.save(profile);

		return user;
	}

//register merchant

	@Transactional(rollbackFor = Exception.class)
	public User createMerchant(MerchantRegisterRequest request) {

		String username = validateUserOnRegister(
				request.getPassword(),
				request.getConfirmPassword(),
				request.getEmail()
		);

		User user = User.builder()
				.username(username)
				.email(request.getEmail())
				.password(passwordEncoder.encode(request.getPassword()))
				.fullName(request.getFullName())
				.role(UserRole.MERCHANT)
				.enabled(true)
				.build();

		userRepository.save(user);

		MerchantProfile profile = MerchantProfile.builder()
				.user(user)
				.location(request.getLocation())
				.NIK(request.getNik())
				.rating(0)
				.build();

		merchantProfileRepository.save(profile);

		return user;
	}

//crud user

	public User getUserById(Long id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User not found!"));
	}

	@Transactional(rollbackFor = Exception.class)
	public User updateUser(Long id, UserRequest request) {
		User user = getUserById(id);

		// duplikat email validasi
		if (request.getEmail() != null && !request.getEmail().isBlank()) {
			if (userRepository.existsByEmailAndIdNot(request.getEmail(), id)) {
				throw new ResourceDuplicateException("Email already exists");
			}
			user.setEmail(request.getEmail());
		}


		if (request.getUsername() != null && !request.getUsername().isBlank()) {
			if (userRepository.existsByUsernameAndIdNot(request.getUsername(), id)) {
				throw new ResourceDuplicateException("Username already exists");
			}
			user.setUsername(request.getUsername());
		}

		// full nama
		if (request.getFullName() != null && !request.getFullName().isBlank()) {
			user.setFullName(request.getFullName());
		}

		//  UPDATE PASSWORD (HANYA JIKA DIISI)
		if (request.getPassword() != null && !request.getPassword().isBlank()) {
			user.setPassword(passwordEncoder.encode(request.getPassword()));
		}

		// profilePicture,
		if (request.getProfilePicture() != null && !request.getProfilePicture().isBlank()) {
			user.setProfilePicture(request.getProfilePicture());
		}

		return userRepository.save(user);
	}

	@Transactional
	public void updateExistingUser(User user, UserRequest request) {
		// Method ni bisa dipake kalo mau inject User yang sudah di-load di tempat lain
		if (request.getFullName() != null && !request.getFullName().isBlank()) {
			user.setFullName(request.getFullName());
		}

		if (request.getEmail() != null && !request.getEmail().isBlank()) {
			user.setEmail(request.getEmail());
		}

		if (request.getProfilePicture() != null && !request.getProfilePicture().isBlank()) {
			user.setProfilePicture(request.getProfilePicture());
		}

		if (request.getPassword() != null && !request.getPassword().isBlank()) {
			user.setPassword(passwordEncoder.encode(request.getPassword()));
		}
	}

	public List<User> getAllUsersByRole(UserRole role) {
		return userRepository.findAllByRole(role);
	}


	@Transactional(rollbackFor = Exception.class)
	public User updateProfilePicture(Long userId, MultipartFile file) {
		User user = getUserById(userId);

		String imageUrl = cloudinaryService.uploadImage(file);
		user.setProfilePicture(imageUrl);

		return userRepository.save(user);
	}

	public ApiResponse<List<UserResponse>> getAllUsers(int page, int size, String role, String search) {

		List<User> allUsers = userRepository.findAll();

		// FILTER BY ROLE
		if (role != null && !role.isEmpty()) {
			allUsers = allUsers.stream()
					.filter(u -> u.getRole() != null &&
							u.getRole().getRoleName().equalsIgnoreCase(role))
					.toList();
		}

		// SEARCH BY NAME OR EMAIL
		if (search != null && !search.isEmpty()) {
			String keyword = search.toLowerCase();
			allUsers = allUsers.stream()
					.filter(u ->
							(u.getFullName() != null &&
									u.getFullName().toLowerCase().contains(keyword))
									||
									(u.getEmail() != null &&
											u.getEmail().toLowerCase().contains(keyword))
					)
					.toList();
		}

		int totalRows = allUsers.size();

		// PAGINATION MANUAL
		int start = page * size;
		int end = Math.min(start + size, totalRows);

		List<User> paginatedUsers = (start < end) ? allUsers.subList(start, end) : List.of();

		List<UserResponse> userResponses = paginatedUsers.stream()
				.map(user -> UserResponse.builder()
						.id(user.getId())
						.username(user.getUsername())
						.email(user.getEmail())
						.fullName(user.getFullName())
						.role(user.getRole())
						.profilePicture(user.getProfilePicture())
						.build())
				.toList();

		int totalPages = (int) Math.ceil((double) totalRows / size);

		PagingResponse paging = PagingResponse.builder()
				.page(page)
				.rowsPerPage(size)
				.totalRows((long) totalRows)
				.totalPages(totalPages)
				.hasNext(page + 1 < totalPages)
				.hasPrevious(page > 0)
				.build();

		return ApiResponse.<List<UserResponse>>builder()
				.status(new StatusResponse(200, "Users fetched successfully"))
				.data(userResponses)
				.paging(paging)
				.build();
	}


	private String validateUserOnRegister(String password, String confirmPassword, String email) {

		// PASSWORD MATCH
		if (!password.equals(confirmPassword)) {
			throw new IllegalArgumentException("Password and confirm password do not match");
		}

		// EMAIL DUPLICATE
		if (userRepository.existsByEmail(email)) {
			throw new ResourceDuplicateException("Email already exists");
		}

		// GENERATE USERNAME FROM EMAIL + ENSURE UNIQUE
		String username = email.split("@")[0];
		int counter = 1;
		String originalUsername = username;

		while (userRepository.existsByUsername(username)) {
			username = originalUsername + counter++;
		}

		return username;
	}


//	get me
@Transactional(readOnly = true)
public UserResponse getMe() {
	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	User principal = (User) auth.getPrincipal();

	// Load ulang user dari DB supaya Hibernate session aktif
	User user = userRepository.findById(principal.getId())
			.orElseThrow(() -> new RuntimeException("User not found"));

	return user.toResponse();
}


}