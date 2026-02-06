package com.example.lostfound.security;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtUtil;

    public JwtAuthFilter(JwtTokenUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

   @Override
protected void doFilterInternal(HttpServletRequest req,
                                HttpServletResponse res,
                                FilterChain chain)
        throws ServletException, IOException {

    String path = req.getRequestURI();

    // ✅ COMPLETELY PUBLIC ENDPOINTS
    if (
        path.equals("/") ||
        path.startsWith("/api/items") ||
        path.startsWith("/api/auth") ||
        path.endsWith(".html") ||
        path.startsWith("/static") ||
        path.startsWith("/api/items/photo")
    ) {
        chain.doFilter(req, res);
        return;
    }

    String header = req.getHeader("Authorization");

    if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
        try {
            String token = header.substring(7);
            String username = jwtUtil.getUsername(token);
            String role = jwtUtil.getRole(token);

            UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    List.of(new SimpleGrantedAuthority(role))
                );

            SecurityContextHolder.getContext().setAuthentication(auth);

        } catch (Exception e) {
            SecurityContextHolder.clearContext();
        }
    }

    chain.doFilter(req, res);
}
}