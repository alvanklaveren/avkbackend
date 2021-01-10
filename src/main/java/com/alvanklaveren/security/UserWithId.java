package com.alvanklaveren.security;

import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * Simple User that also keep track of the primary key.
 */
@Getter(AccessLevel.PUBLIC)
public class UserWithId extends User {

    private static final long serialVersionUID = 1L;
    private final Integer id;

    public UserWithId(String username, String password, boolean accountNonExpired, boolean credentialsNonExpired,
                      boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities, Integer id) {

        super(username, password, true, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.id = id;
    }
}