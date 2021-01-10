package com.alvanklaveren.security;

import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter(AccessLevel.PUBLIC)
public class AuthenticationToken extends UsernamePasswordAuthenticationToken {

    private static final long serialVersionUID = 1L;

    private final Integer userCode;

    public AuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities,
            Integer userCode) {

        super(principal, credentials, authorities);
        this.userCode = userCode;
    }
}
