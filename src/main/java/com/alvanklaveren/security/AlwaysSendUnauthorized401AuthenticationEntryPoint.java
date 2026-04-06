package com.alvanklaveren.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AlwaysSendUnauthorized401AuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public final void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {

        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
