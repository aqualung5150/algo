package com.seungjoon.algo;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = CustomMockUserSecurityContextFactory.class)
public @interface WithMockMember {
    long id() default 1L;
    String role() default "MEMBER";
}
