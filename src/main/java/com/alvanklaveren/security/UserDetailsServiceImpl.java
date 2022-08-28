package com.alvanklaveren.security;

import com.alvanklaveren.enums.EClassification;
import com.alvanklaveren.model.ForumUser;
import com.alvanklaveren.repository.ForumUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.alvanklaveren.security.SecurityConstants.*;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger LOG = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    private ForumUserRepository forumUserRepository;

    @Autowired
    public UserDetailsServiceImpl(ForumUserRepository forumUserRepository) {
        this.forumUserRepository = forumUserRepository;
    }

    /**
     * Retrieve an account depending on its login this method is not case sensitive.
     *
     * @param username the user's username
     * @return a Spring Security userdetails object that matches the username
     * @throws UsernameNotFoundException when the user could not be found
     */
    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) {

        if (username == null || username.trim().isEmpty()) {

            throw new UsernameNotFoundException("Empty username");
        }
        LOG.debug("Security verification for user '{}'", username);
        ForumUser forumUser = forumUserRepository.getByUsername(username);
        if (forumUser == null) {

            LOG.info("User {} could not be found", username);
            throw new UsernameNotFoundException("user " + username + " could not be found");
        }

        Collection<GrantedAuthority> grantedAuthorities;
        Set<String> roles = new HashSet<>();
        switch(forumUser.getClassification().getCode()){
            case 1 -> roles.add(EClassification.Administrator.getRoleName());
            case 2 -> roles.add(EClassification.Member.getRoleName());
            default -> roles.add(EClassification.Guest.getRoleName()); /* = also case 3*/
        }

        grantedAuthorities = toGrantedAuthorities(roles);

        String password = forumUser.getPassword();
        boolean accountNonExpired = true;
        boolean credentialsNonExpired = true;
        boolean accountNonLocked = true;
        return new UserWithId(username, password, accountNonExpired, credentialsNonExpired, accountNonLocked, grantedAuthorities, forumUser.getCode());
    }

    private Collection<GrantedAuthority> toGrantedAuthorities(Set<String> roles) {

        List<GrantedAuthority> result = new ArrayList<>();
        for (String role : roles) {

            result.add(new SimpleGrantedAuthority(role));
        }
        return result;
    }
}