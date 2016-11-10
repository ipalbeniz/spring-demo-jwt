package com.example.security;

import com.example.model.Permission;
import com.example.model.User;
import com.example.service.SecurityService;
import com.example.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Aspect
@Component
public class SecuredProcessor {

	private static final Logger logger = LoggerFactory.getLogger(SecuredProcessor.class);

	private static final String AUTH_TYPE = "Bearer";

	private final UserService userService;
	private final SecurityService securityService;

	@Autowired
	public SecuredProcessor(UserService userService, SecurityService securityService) {
		this.userService = userService;
		this.securityService = securityService;
	}

	@Before("@annotation(com.example.security.Secured)")
	public void checkSecurity(JoinPoint point) throws Throwable {

		String token = getTokenFromHeader();

		Jws<Claims> claims = securityService.parseToken(token);

		checkPermissions(claims, point);

		logger.debug("Security OK. User: {}, Expiration: {}", claims.getBody().getSubject(), claims.getBody().getExpiration());
	}

	private String getTokenFromHeader() {

		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
		String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

		return StringUtils.replace(authorizationHeader, AUTH_TYPE, "").trim();
	}

	private void checkPermissions(Jws<Claims> claims, JoinPoint point) {

		Set<Permission> requiredPermissions = getRequiredPermissions(point);

		if (!CollectionUtils.isEmpty(requiredPermissions)) {

			String username = claims.getBody().getSubject();

			User user = userService.getByUsername(username)
					.orElseThrow(() -> new AuthorizationException("User not found"));

			if (!user.getPermissions().containsAll(requiredPermissions)) {

				throw new AuthorizationException("The user is unauthorized");
			}
		}

	}

	private Set<Permission> getRequiredPermissions(JoinPoint point) {

		MethodSignature signature = (MethodSignature) point.getSignature();
		Secured secured = signature.getMethod().getAnnotation(Secured.class);

		return Stream.of(secured.value()).collect(Collectors.toSet());
	}
}