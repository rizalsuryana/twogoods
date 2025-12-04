//package com.finpro.twogoods.service;
//
//import com.finpro.twogoods.entity.User;
//import com.finpro.twogoods.model.request.*;
//import com.finpro.twogoods.model.response.LoginResponse;
//import com.finpro.twogoods.model.response.RegisterResponse;
//import com.finpro.twogoods.security.JwtTokenProvider;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.stereotype.Service;
//
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class AuthService {
//    private final JwtTokenProvider jwtTokenProvider;
//    private final AuthenticationManager authenticationManager;
//    private final UserService userService;
//
//    public LoginResponse login(LoginRequest request) {
//        Authentication authentication = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(
//                        request.getEmail(),
//                        request.getPassword())
//        );
//
//        User user = (User) authentication.getPrincipal();
//        String token = jwtTokenProvider.generateToken(user);
//        String TOKEN_TYPE = "Bearer";
//        return LoginResponse.builder()
//                .accessToken(token)
//                .tokenType(TOKEN_TYPE)
//                .role(user.getRole().getRoleName())
//                .build();
//    }
//
//    public RegisterResponse register(RegisterRequest request) {
//        log.info("Attempting to register new user with email: {}", request.getEmail());
//
//        User user = userService.createUser(request);
//
//        log.info("User registered successfully with username: {}", user.getUsername());
//        return RegisterResponse.builder()
//                .email(user.getEmail())
//                .build();
//    }
//
//}
