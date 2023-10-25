package com.example.UserAuthenticationManagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserException extends RuntimeException {
	
	private HttpStatus status;
	private String error;
	private String message;
	

}
