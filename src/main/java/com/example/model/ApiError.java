package com.example.model;

import java.time.LocalDateTime;

public class ApiError {

	private LocalDateTime date;
	private String message;

	public ApiError(String message) {
		this.date = LocalDateTime.now();
		this.message = message;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
