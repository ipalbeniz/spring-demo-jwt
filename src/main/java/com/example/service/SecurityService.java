package com.example.service;

import com.example.model.TokenRequest;
import com.example.model.TokenResponse;
import com.example.security.AuthenticationException;
import com.example.security.AuthorizationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
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

@Service
public class SecurityService {

	private static final String USER = "iperez";
	private static final String PASSWORD = "1234";
	private static final String REFRESH_TOKEN_CLAIM_KEY = "refresh";

	@Value("${jwt.accessTokenTimeToLiveSeconds}")
	private int accessTokenTimeToLiveSeconds;

	@Value("${jwt.refreshTokenTimeToLiveSeconds}")
	private int refreshTokenTimeToLiveSeconds;

	@Value("${jwt.privateKey}")
	private String privateKey;

	@Value("${jwt.tokenType}")
	private String tokenType;

	private final UserService userService;

	@Autowired
	public SecurityService(UserService userService) {
		this.userService = userService;
	}

	public TokenResponse authenticate(TokenRequest tokenRequest) {

		if (USER.equals(tokenRequest.getUser()) && PASSWORD.equals(tokenRequest.getPassword())) {

			String accessToken = createAccessJwtToken(tokenRequest.getUser());
			String refreshToken = createRefreshJwtToken(tokenRequest.getUser());

			return buildAuthResponse(accessToken, refreshToken);

		} else {

			throw new AuthenticationException("User unable to authenticate");
		}
	}

	public Jws<Claims> parseAndValidateAccessToken(String accessToken) {

		Jws<Claims> claims = Jwts.parser().setSigningKey(privateKey).parseClaimsJws(accessToken);

		Boolean isRefreshToken = claims.getBody().get(REFRESH_TOKEN_CLAIM_KEY, Boolean.class);

		if (isRefreshToken != null && isRefreshToken) {
			throw new AuthorizationException("Invalid access token");
		}

		return claims;
	}

	private Jws<Claims> parseAndValidateRefreshToken(String refreshToken) {

		Jws<Claims> claims = Jwts.parser().setSigningKey(privateKey).parseClaimsJws(refreshToken);

		Boolean isRefreshToken = claims.getBody().get(REFRESH_TOKEN_CLAIM_KEY, Boolean.class);

		if (isRefreshToken == null || !isRefreshToken) {
			throw new AuthorizationException("Invalid refresh token");
		}

		return claims;
	}

	public TokenResponse validateRefreshTokenAndIssueNewAccessToken(String refreshToken) {

		Jws<Claims> claims = parseAndValidateRefreshToken(refreshToken);

		String newAccessToken = createAccessJwtToken(claims.getBody().getSubject());

		return buildAuthResponse(newAccessToken, refreshToken);
	}

	private String createAccessJwtToken(String subject) {

		LocalDateTime tokenExpirationDateTime = LocalDateTime.now()
				.plus(accessTokenTimeToLiveSeconds, ChronoUnit.SECONDS);

		return createJwtToken(subject, tokenExpirationDateTime, false);
	}

	private String createRefreshJwtToken(String subject) {

		LocalDateTime tokenExpirationDateTime = LocalDateTime.now()
				.plus(refreshTokenTimeToLiveSeconds, ChronoUnit.SECONDS);

		return createJwtToken(subject, tokenExpirationDateTime, true);
	}

	private String createJwtToken(String subject, LocalDateTime tokenExpirationDateTime, boolean isRefreshToken) {

		JwtBuilder jwtBuilder = Jwts.builder()
				.setSubject(subject)
				.signWith(SignatureAlgorithm.HS512, privateKey)
				.setExpiration(toDate(tokenExpirationDateTime));

		if (isRefreshToken) {
			jwtBuilder.claim(REFRESH_TOKEN_CLAIM_KEY, true);
		}

		return jwtBuilder.compact();
	}

	private Date toDate(LocalDateTime localDateTime) {

		ZoneOffset zoneOffset = ZoneId.systemDefault().getRules().getOffset(localDateTime);
		return Date.from(localDateTime.toInstant(zoneOffset));
	}

	private TokenResponse buildAuthResponse(String token, String refreshToken) {

		TokenResponse tokenResponse = new TokenResponse();

		tokenResponse.setExpires_in(accessTokenTimeToLiveSeconds);
		tokenResponse.setAccess_token(token);
		tokenResponse.setRefresh_token(refreshToken);
		tokenResponse.setToken_type(tokenType);

		return tokenResponse;
	}
}
