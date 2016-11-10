package com.example.security;

import com.example.model.Permission;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Secured {

	Permission[] value() default {};
}