package com.example.service;

import com.example.model.AuthRequest;
import com.example.model.AuthResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

@Service
public class SecurityService {

	private static final String USER = "iperez";
	private static final String PASSWORD = "1234";

	@Value("${jwt.timeToLive}")
	private long timeToLive;

	@Value("${jwt.privateKey}")
	private String privateKey;

	private final UserService userService;

	@Autowired
	public SecurityService(UserService userService) {
		this.userService = userService;
	}

	public Optional<AuthResponse> authenticate(AuthRequest authRequest) {

		if (USER.equals(authRequest.getUser()) && PASSWORD.equals(authRequest.getPassword())) {

			LocalDateTime tokenExpirationDateTime = calculateTokenExpirationTime();

			String token = createJwtToken(authRequest, tokenExpirationDateTime);

			AuthResponse authResponse = new AuthResponse();
			authResponse.setExpirationDate(tokenExpirationDateTime);
			authResponse.setToken(token);

			return Optional.of(authResponse);

		} else {

			return Optional.empty();
		}
	}

	public Jws<Claims> parseToken(String token) {

		return Jwts.parser().setSigningKey(privateKey).parseClaimsJws(token);
	}

	private LocalDateTime calculateTokenExpirationTime() {

		return LocalDateTime.now().plus(timeToLive, ChronoUnit.MINUTES);
	}

	private String createJwtToken(AuthRequest authRequest, LocalDateTime tokenExpirationDateTime) {

		return Jwts.builder()
				.setSubject(authRequest.getUser())
				.signWith(SignatureAlgorithm.HS512, privateKey)
				.setExpiration(toDate(tokenExpirationDateTime))
				.compact();
	}

	private Date toDate(LocalDateTime localDateTime) {

		ZoneOffset zoneOffset = ZoneId.systemDefault().getRules().getOffset(localDateTime);
		return Date.from(localDateTime.toInstant(zoneOffset));
	}
}
