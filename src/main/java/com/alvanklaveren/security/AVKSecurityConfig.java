package com.alvanklaveren.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class AVKSecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private TokenAuthenticationService tokenAuthenticationService;

    @Autowired
    private AlwaysSendUnauthorized401AuthenticationEntryPoint alwaysSendUnauthorized401AuthenticationEntryPoint;


    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder,
                                                            UserDetailsService userDetailsService) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider() {
            @Override
            protected void additionalAuthenticationChecks(UserDetails userDetails,
                                                          UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
                String newCredentials = authentication.getCredentials().toString()
                        .replace(authentication.getPrincipal().toString(), userDetails.getUsername());
                super.additionalAuthenticationChecks(userDetails,
                        new UsernamePasswordAuthenticationToken(userDetails.getUsername(), newCredentials));
            }
        };
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return authenticationProvider;
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfiguration) throws Exception {
        return authConfiguration.getAuthenticationManager();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().
                requestMatchers(
                        new AntPathRequestMatcher("/"),
                        new AntPathRequestMatcher("/backend/translation/**"),
                        new AntPathRequestMatcher("/v2/api-docs"),
                        new AntPathRequestMatcher("/webjars/**"),
                        new AntPathRequestMatcher("/*.{js,html}"),
                        new AntPathRequestMatcher("/img/**"),
                        new AntPathRequestMatcher("/node_modules/**"),
                        new AntPathRequestMatcher("/**/*.{js,html,css}")
                );
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager,
                                           AuthenticationProvider authenticationProvider, TokenAuthenticationService tokenAuthenticationService,
                                           AlwaysSendUnauthorized401AuthenticationEntryPoint alwaysSendUnauthorized401AuthenticationEntryPoint)
            throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authenticationProvider(authenticationProvider)
                .authorizeHttpRequests((authReq) -> authReq
                        .requestMatchers(new AntPathRequestMatcher("/"),
                                new AntPathRequestMatcher("/backend/**"),
                                new AntPathRequestMatcher("/backend/dailies/**"),
                                new AntPathRequestMatcher("/backend/forum/**"),
                                new AntPathRequestMatcher("/backend/gameshop/**"),
                                new AntPathRequestMatcher("/backend/translation/**"),
                                new AntPathRequestMatcher("/backend/translation/dictionary/**")
                        ).permitAll()
                        .anyRequest().authenticated())
                // We filter the /login requests
                .addFilterBefore(new JWTAuthenticationFilter("/backend/login", authenticationManager, tokenAuthenticationService), UsernamePasswordAuthenticationFilter.class)
                // And filter other requests to check the presence of JWT in header
                .addFilterBefore(new JWTAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception -> exception.authenticationEntryPoint(alwaysSendUnauthorized401AuthenticationEntryPoint))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH"));
        configuration.setAllowedHeaders(
                List.of(HttpHeaders.AUTHORIZATION, HttpHeaders.CONTENT_TYPE, HttpHeaders.CONTENT_LENGTH));
        configuration.setAllowCredentials(Boolean.TRUE);
        configuration.setMaxAge(180L);
        configuration.setExposedHeaders(
                List.of(HttpHeaders.AUTHORIZATION, HttpHeaders.CONTENT_TYPE, HttpHeaders.CONTENT_LENGTH));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


    @Bean
    public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
        return new SecurityEvaluationContextExtension();
    }

}