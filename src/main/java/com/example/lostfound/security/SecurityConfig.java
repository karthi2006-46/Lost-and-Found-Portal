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

            // ✅ PUBLIC HTML
            .requestMatchers(
                "/", "/index.html",
                "/login.html", "/register.html",
                "/item.html", "/post.html",
                "/my-claims.html",
                "/admin-claims.html",   // ✅ ALLOW HTML
                "/static/**"
            ).permitAll()

            // ✅ PUBLIC APIs
            .requestMatchers("/api/items/**", "/api/auth/**").permitAll()

            // 🔐 ADMIN APIs ONLY
            .requestMatchers("/api/claims/pending", "/api/claims/*/resolve")
            .hasAuthority("ADMIN")

            // 🔐 ALL OTHER APIs NEED LOGIN
            .anyRequest().authenticated()
        )
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
}
}