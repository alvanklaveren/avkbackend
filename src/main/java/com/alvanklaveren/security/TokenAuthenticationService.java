package com.alvanklaveren.security;

import com.alvanklaveren.model.ForumUser;
import com.alvanklaveren.repository.ForumUserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

import static com.alvanklaveren.security.SecurityConstants.*;

@Component
public class TokenAuthenticationService {

    @Autowired private ForumUserRepository forumUserRepository;

    @Transactional
    public void addAuthentication(HttpServletResponse res, String username) throws JsonProcessingException {

        ForumUser forumUser = forumUserRepository.getByUsername(username);

        List<String> roles = new ArrayList<>();
        switch(forumUser.getClassification().getCode()){
            case 1: roles.add(ROLE_ADMIN); break;
            case 2: roles.add(ROLE_MEMBER); break;
            case 3: default: roles.add(ROLE_GUEST); break;
        }

        HashMap<String, Object> claims = new HashMap<>();
        claims.put("userCode", forumUser.getCode());
        claims.put("roles", roles);

        String accessToken = SecurityConstants.TOKEN_PREFIX + Jwts.builder().setClaims(claims).setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.SECRET).compact();

        Date refreshTokenExp = new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME * 2);

        String refreshToken = SecurityConstants.TOKEN_PREFIX + Jwts.builder()
                .setClaims(claims).setSubject(username)
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(new Date())
                .setExpiration(refreshTokenExp)
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.SECRET)
                .compact();

        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("accessToken", accessToken);
        tokenMap.put("refreshToken", refreshToken);

        String auth = new ObjectMapper().writeValueAsString(tokenMap);

        res.setHeader(SecurityConstants.HEADER_STRING, auth);
    }

    static Authentication getAuthentication(HttpServletRequest request, HttpServletResponse response) {

        AuthenticationToken authenticationToken = null;

        String token = request.getHeader(SecurityConstants.HEADER_STRING);
        if (token != null) {

            Claims claims = (Jwts.parser().setSigningKey(SecurityConstants.SECRET)
                    .parseClaimsJws(token.replace(SecurityConstants.TOKEN_PREFIX, "")).getBody());

            List<String> roles = claims.get("roles", List.class);
            List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(roles.toArray(new String[roles.size()]));

            if (claims.getSubject() != null) {
                authenticationToken = new AuthenticationToken(claims.getSubject(), null, authorities,
                        claims.get("userCode", Integer.class));
            }
        }

        return authenticationToken;
    }

}