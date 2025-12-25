package com.example.lostfound.security;

import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtTokenUtil jwtUtil;

    public SecurityConfig(JwtTokenUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    JwtAuthFilter jwtFilter = new JwtAuthFilter(jwtUtil);

    http.csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(
                "/",
                "/index.html",
                "/item.html",
                "/post.html",
                "/login.html",
                "/register.html",
                "/static/**",
                "/api/auth/**",
                "/api/items",           // list
                "/api/items/*",         // single item  👈 IMPORTANT
                "/api/items/photo/**"   // image
            ).permitAll()
            .requestMatchers(HttpMethod.POST, "/api/items").authenticated()
            .anyRequest().authenticated()
        )
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
}
}