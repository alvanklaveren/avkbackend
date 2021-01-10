package com.alvanklaveren.security;

import com.alvanklaveren.enums.EClassification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;

/**
 * Get Spring security context to access user data security infos
 */
public final class UserContext {

    public static final String ANONYMOUS_USER = "anonymousUser";

    private UserContext() { }

    /**
     * Get the current username. Note that it may not correspond to a username that
     * currently exists in your account repository; it could be a spring security
     * 'anonymous user'.
     *
     * @see org.springframework.security.web.authentication.AnonymousAuthenticationFilter
     * @return the current user's username, or 'anonymousUser'.
     */
    public static String getUsername() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return ANONYMOUS_USER;
        }

        Object principal = auth.getPrincipal();
        return principal instanceof UserDetails ? ((UserDetails) principal).getUsername() : principal.toString();
    }

    public static Integer getId() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {

            return null;
        }

        if (auth instanceof AuthenticationToken) {
            return ((AuthenticationToken)auth).getUserCode();
        }

        return null;
    }

    /**
     * Retrieve the current UserDetails bound to the current thread by Spring Security, if any.
     */
    public static UserDetails getUserDetails() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getPrincipal() instanceof UserDetails ? ((UserDetails) auth.getPrincipal()) : null;
    }

    /**
     * Return the current roles bound to the current thread by Spring Security.
     */
    public static List<String> getRoles() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth == null ? emptyList() : toStringList(auth.getAuthorities());
    }

    /**
     * Tell whether the passed role is set?
     *
     * @return true if the passed role is present, false otherwise.
     */
    public static boolean hasRole(EClassification eClassification) {

        return getRoles().stream().anyMatch(role -> role.equalsIgnoreCase(eClassification.getRoleName()));
    }

    public static List<String> toStringList(Iterable<? extends GrantedAuthority> grantedAuthorities) {

        List<String> result = new ArrayList<>();
        grantedAuthorities.forEach(grantedAuthority -> result.add(grantedAuthority.getAuthority()));
        return result;
    }
}