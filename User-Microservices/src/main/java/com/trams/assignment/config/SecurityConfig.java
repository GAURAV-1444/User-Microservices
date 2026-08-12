package com.trams.assignment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.trams.assignment.security.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

    @SuppressWarnings("removal")
	@Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http.csrf(Customizer.withDefaults());

        http.csrf().disable();

        http.sessionManagement()
                .sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS
                );

        http.authorizeHttpRequests()
                .requestMatchers(
                        "/api/users/register",
                        "/api/users/login",
                        "/test/jwt"
                )
                .permitAll()
                .anyRequest()
                .authenticated();

        http.exceptionHandling()
                .authenticationEntryPoint(
                        authenticationEntryPoint
                );

        http.addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }
}