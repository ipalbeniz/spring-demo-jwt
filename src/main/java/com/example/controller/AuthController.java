package com.example.controller;

import com.example.aop.Secured;
import com.example.model.AuthRequest;
import com.example.model.AuthResponse;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@RestController
public class AuthController {

    private static final long TOKEN_TIME_TO_LIVE = 30;

    private static final String USER = "iperez";
    private static final String PASSWORD = "1234";

    @Value("${sign.key}")
    private String SIGN_KEY;

    @GetMapping("/auth")
    public ResponseEntity<AuthResponse> authentication(@ModelAttribute AuthRequest authRequest) {

        if (authenticated(authRequest)) {

            LocalDateTime tokenExpirationDateTime = LocalDateTime.now().plus(TOKEN_TIME_TO_LIVE, ChronoUnit.MINUTES);

            String token = Jwts.builder()
                    .setSubject(authRequest.getUser())
                    .signWith(SignatureAlgorithm.HS512, SIGN_KEY)
                    .setExpiration(toDate(tokenExpirationDateTime))
                    .compact();

            AuthResponse authResponse = new AuthResponse();
            authResponse.setExpirationDate(tokenExpirationDateTime);
            authResponse.setToken(token);

            return ResponseEntity.ok(authResponse);

        } else {

            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

    }

    @Secured
    @GetMapping("/secured-message")
    public String securedMessage() {

        return "Hello secured world!";

    }

    private Date toDate(LocalDateTime localDateTime) {

        ZoneOffset zoneOffset = ZoneId.systemDefault().getRules().getOffset(localDateTime);
        return Date.from(localDateTime.toInstant(zoneOffset));
    }

    private boolean authenticated(AuthRequest authRequest) {

        return authRequest != null &&
                USER.equals(authRequest.getUser()) &&
                PASSWORD.equals(authRequest.getPassword());
    }
}
