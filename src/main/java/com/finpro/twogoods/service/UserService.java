package com.finpro.twogoods.service;

import com.finpro.twogoods.entity.User;
import com.finpro.twogoods.entity.UserRole;
import com.finpro.twogoods.exception.ResourceDuplicateException;
import com.finpro.twogoods.model.request.CustomerRegisterRequest;
import com.finpro.twogoods.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository  userRepository;
	private final PasswordEncoder passwordEncoder;


    public User createUser (CustomerRegisterRequest request) {
        if(userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceDuplicateException("Email already exists");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
		        .name(request.getName())
                .role(UserRole.CUSTOMER)
                .build();
        return userRepository.saveAndFlush(user);
    }

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		return userRepository.findByEmail(email)
		                     .orElseThrow(() -> new UsernameNotFoundException("User not found"));
	}
}