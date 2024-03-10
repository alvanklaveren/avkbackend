package com.alvanklaveren.security;

import io.jsonwebtoken.JwtException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JWTAuthorizationFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {

        //HttpServletRequest rq = (HttpServletRequest) request;
        //ServletContext sc = rq.getServletContext();

        try {

            Authentication authentication = TokenAuthenticationService.getAuthentication((HttpServletRequest) request,
                    (HttpServletResponse) response);

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (JwtException ejwt) {

            //log.error("Exception: ", ejwt);

            ((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED, ejwt.getMessage());
            return; // SKIP FILTER CHAIN
        }
        filterChain.doFilter(request, response);
    }

}