package com.example.controller;

import com.example.model.ApiError;
import com.example.security.AuthenticationException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class ExceptionController {

	private static final Logger logger = LoggerFactory.getLogger(ExceptionController.class);

	@ExceptionHandler(JwtException.class)
	@ResponseStatus(value = HttpStatus.FORBIDDEN)
	@ResponseBody
	public ApiError jwtException(JwtException exception) {

		String message;

		if (exception instanceof ExpiredJwtException) {

			message = "Expired token";

		} else {

			message = "Invalid token";
		}

		return new ApiError(message);
	}

	@ExceptionHandler(AuthenticationException.class)
	@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
	@ResponseBody
	public ApiError authenticationException(Throwable exception) {

		return new ApiError(exception.getMessage());
	}

	@ExceptionHandler(Throwable.class)
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public ApiError throwable(Throwable exception) {

		logger.error("Unexpected Exception", exception);

		return new ApiError(exception.getMessage());
	}

}
