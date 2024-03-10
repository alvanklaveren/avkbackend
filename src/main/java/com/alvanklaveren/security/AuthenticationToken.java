package com.alvanklaveren.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class AuthenticationToken extends UsernamePasswordAuthenticationToken {

    private static final long serialVersionUID = 1L;

    private final Integer userCode;

    public AuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities,
            Integer userCode) {

        super(principal, credentials, authorities);
        this.userCode = userCode;
    }

    public Integer getUserCode() {
        return userCode;
    }
}
