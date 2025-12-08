package com.finpro.twogoods.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;
import org.springframework.http.converter.HttpMessageNotReadableException;

import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	private ResponseEntity<Map<String, String>> build(HttpStatus status, String message) {
		Map<String, String> error = new HashMap<>();
		error.put("error", message);
		return ResponseEntity.status(status).body(error);
	}

	//   1. Resource Not Found
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<Map<String, String>> handleNotFound(ResourceNotFoundException ex) {
		log.error("Not found: {}", ex.getMessage());
		return build(HttpStatus.NOT_FOUND, ex.getMessage());
	}

	//   2. Duplicate Resource
	@ExceptionHandler(ResourceDuplicateException.class)
	public ResponseEntity<Map<String, String>> handleDuplicate(ResourceDuplicateException ex) {
		log.error("Duplicate: {}", ex.getMessage());
		return build(HttpStatus.BAD_REQUEST, ex.getMessage());
	}

	//   3. Invalid File
	@ExceptionHandler(InvalidFileException.class)
	public ResponseEntity<Map<String, String>> handleInvalidFile(InvalidFileException ex) {
		log.error("Invalid file: {}", ex.getMessage());
		return build(HttpStatus.BAD_REQUEST, ex.getMessage());
	}

	//   4. Validation Errors (@Valid)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
		log.error("Validation error: {}", ex.getMessage());
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getFieldErrors().forEach(err ->
				errors.put(err.getField(), err.getDefaultMessage())
		);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
	}

	//   5. Illegal Argument (password mismatch, invalid input)
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
		log.error("Illegal argument: {}", ex.getMessage());
		return build(HttpStatus.BAD_REQUEST, ex.getMessage());
	}

	//   6. Rate Limit
	@ExceptionHandler(RateLimitException.class)
	public ResponseEntity<Map<String, String>> handleRateLimit(RateLimitException ex) {
		log.error("Rate limit: {}", ex.getMessage());
		return build(HttpStatus.TOO_MANY_REQUESTS, ex.getMessage());
	}

	//  7. Multipart / File Upload Error
	@ExceptionHandler(MultipartException.class)
	public ResponseEntity<Map<String, String>> handleMultipart(MultipartException ex) {
		log.error("Multipart error: {}", ex.getMessage());
		return build(HttpStatus.BAD_REQUEST, "Invalid or too large file upload");
	}

	// 8. JSON Parse Error
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<Map<String, String>> handleJsonParse(HttpMessageNotReadableException ex) {
		log.error("JSON parse error: {}", ex.getMessage());
		return build(HttpStatus.BAD_REQUEST, "Invalid JSON format");
	}

	// 9. Missing Request Parameter
	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<Map<String, String>> handleMissingParam(MissingServletRequestParameterException ex) {
		log.error("Missing parameter: {}", ex.getMessage());
		return build(HttpStatus.BAD_REQUEST, ex.getMessage());
	}

	// 10. Constraint Violation (@NotNull, @Size, etc)
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<Map<String, String>> handleConstraintViolation(ConstraintViolationException ex) {
		log.error("Constraint violation: {}", ex.getMessage());
		return build(HttpStatus.BAD_REQUEST, ex.getMessage());
	}

	//   11. Method Not Allowed
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<Map<String, String>> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
		log.error("Method not allowed: {}", ex.getMessage());
		return build(HttpStatus.METHOD_NOT_ALLOWED, "Method not allowed");
	}

	// 12. Endpoint Not Found
	@ExceptionHandler(org.springframework.web.servlet.NoHandlerFoundException.class)
	public ResponseEntity<Map<String, String>> handleNoHandler(org.springframework.web.servlet.NoHandlerFoundException ex) {
		log.error("Endpoint not found: {}", ex.getMessage());
		return build(HttpStatus.NOT_FOUND, "Endpoint not found");
	}

	// 13. Database Constraint Error
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<Map<String, String>> handleDataIntegrity(DataIntegrityViolationException ex) {
		log.error("DB constraint error: {}", ex.getMessage());
		return build(HttpStatus.BAD_REQUEST, "Database constraint violation");
	}

	// 14. Authentication Errors
	@ExceptionHandler({BadCredentialsException.class, UsernameNotFoundException.class})
	public ResponseEntity<Map<String, String>> handleBadCredentials(Exception ex) {
		log.error("Bad credentials: {}", ex.getMessage());
		return build(HttpStatus.UNAUTHORIZED, "Unauthorized");
	}

	// 15. Account Locked / Disabled
	@ExceptionHandler({LockedException.class, DisabledException.class})
	public ResponseEntity<Map<String, String>> handleAccountLocked(Exception ex) {
		log.error("Account locked/disabled: {}", ex.getMessage());
		return build(HttpStatus.FORBIDDEN, "Forbidden Resource");
	}

	// 16. JWT Errors
	@ExceptionHandler(JwtAuthenticationException.class)
	public ResponseEntity<Map<String, String>> handleJwt(JwtAuthenticationException ex) {
		log.error("JWT error: {}", ex.getMessage());
		return build(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
	}

	// 17. Fallback (General Exception)
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, String>> handleGeneral(Exception ex) {
		if (ex instanceof AccessDeniedException) throw (AccessDeniedException) ex;
		if (ex instanceof AuthenticationException) throw (AuthenticationException) ex;

		log.error("Internal error: {}", ex.getMessage(), ex);
		return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
	}
}
