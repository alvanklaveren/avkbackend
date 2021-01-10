package com.alvanklaveren.security;

public class SecurityConstants {

    public static final String SECRET = "ThisMightBeALittleOverkillButOkay";
    public static final long EXPIRATION_TIME = 2 * 60 * 60 * 1000; // 2 hours

    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
}