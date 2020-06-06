package com.alvanklaveren.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

public class JWTAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

	private TokenAuthenticationService tokenAuthenticationService;

	public JWTAuthenticationFilter(String url, AuthenticationManager authManager, TokenAuthenticationService tokenAuthenticationService) {

		super(new AntPathRequestMatcher(url));
		setAuthenticationManager(authManager);
		this.tokenAuthenticationService = tokenAuthenticationService;
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {

		// transform the request into a cached request, so we can read it as often as we want
		CachedBodyHttpServletRequest cachedRequest = new CachedBodyHttpServletRequest( (HttpServletRequest) req);

		super.doFilter(cachedRequest, res, chain);
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
			throws AuthenticationException, IOException, ServletException {

		AccountCredentials accountCredentials = new ObjectMapper().readValue(req.getInputStream(), AccountCredentials.class);

		try {
				return getAuthenticationManager().authenticate(
					new UsernamePasswordAuthenticationToken(accountCredentials.getUsername(),
					accountCredentials.getUsername() + accountCredentials.getPassword(), Collections.emptyList()));
		} catch(BadCredentialsException bce){
			throw bce;
		} catch(AuthenticationException ae){
			throw ae;
		}
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain,
			Authentication auth) throws IOException, ServletException {

		this.tokenAuthenticationService.addAuthentication(res, auth.getName());
	}


}