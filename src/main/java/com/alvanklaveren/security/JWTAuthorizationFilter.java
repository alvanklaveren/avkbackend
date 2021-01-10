package com.alvanklaveren.security;

import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class JWTAuthorizationFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest rq = (HttpServletRequest) request;

        ServletContext sc = rq.getServletContext();

        try {

            Authentication authentication = TokenAuthenticationService.getAuthentication((HttpServletRequest) request,
                    (HttpServletResponse) response);

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (JwtException ejwt) {

            log.error("Exception: ", ejwt);

            ((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED, ejwt.getMessage());
            return; // SKIP FILTER CHAIN
        }
        filterChain.doFilter(request, response);
    }

}