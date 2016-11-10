package com.example.controller;

import com.example.model.AuthRequest;
import com.example.model.AuthResponse;
import com.example.model.Permission;
import com.example.security.AuthenticationException;
import com.example.security.Secured;
import com.example.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

	private final SecurityService securityService;

	@Autowired
	public DemoController(SecurityService securityService) {
		this.securityService = securityService;
	}

	@GetMapping("/auth")
	public ResponseEntity<AuthResponse> authenticate(@ModelAttribute AuthRequest authRequest) {

		AuthResponse authResponse = securityService.authenticate(authRequest)
				.orElseThrow(() -> new AuthenticationException("User unable to authenticate"));

		return ResponseEntity.ok(authResponse);
	}

	@Secured
	@GetMapping("/refresh")
	public ResponseEntity<AuthResponse> refresh() {

		// TODO: implement the token refresh operation

		return ResponseEntity.ok(null);
	}

	@Secured({Permission.READ_MESSAGE})
	@GetMapping("/secured-message")
	public String securedMessage() {

		return "Hello secured world!";
	}

}
