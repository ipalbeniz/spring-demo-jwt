package com.example.aop;

import io.jsonwebtoken.Jwts;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
public class SecuredAspect {

    @Value("${sign.key}")
    private String SIGN_KEY;

    private static final Logger logger = LoggerFactory.getLogger(SecuredAspect.class);

    @Before("@annotation(com.example.aop.Secured)")
    public void checkSecurityToken(JoinPoint point) throws Throwable {

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String token = request.getHeader("Authorization");

        Jwts.parser().setSigningKey(SIGN_KEY).parseClaimsJws(token);
    }
}