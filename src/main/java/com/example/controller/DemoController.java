package com.example.controller;

import com.example.model.Permission;
import com.example.model.TokenRequest;
import com.example.model.TokenResponse;
import com.example.security.Secured;
import com.example.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

	private final SecurityService securityService;

	@Autowired
	public DemoController(SecurityService securityService) {
		this.securityService = securityService;
	}

	@GetMapping("/token")
	public ResponseEntity<TokenResponse> token(@ModelAttribute TokenRequest tokenRequest) {

		TokenResponse tokenResponse = securityService.authenticate(tokenRequest);

		return ResponseEntity.ok(tokenResponse);
	}

	@GetMapping("/refresh-token")
	public ResponseEntity<TokenResponse> refresh(@RequestParam("refresh_token") String refreshToken) {

		TokenResponse tokenResponse = securityService.validateRefreshTokenAndIssueNewAccessToken(refreshToken);

		return ResponseEntity.ok(tokenResponse);
	}

	@Secured({Permission.READ_MESSAGE})
	@GetMapping("/secured-message")
	public String securedMessage() {

		return "Hello secured world!";
	}

}
