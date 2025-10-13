package com.example.chat.utils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    private final JwtUtil jwtUtil;

    @Autowired
    private final UserDetailsService userDetailsService;

    public JwtFilter(JwtUtil jwtUtil, UserDetailsService uds) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = uds;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        String path = req.getRequestURI();

        if (path.startsWith("/public") || path.startsWith("/auth") || path.equals("/user/checkLogin")) {
            chain.doFilter(req, res);
            return;
        }

        String jwt = null;
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("JWT_TOKEN")) {
                    jwt = cookie.getValue();
                    break;
                }
            }
        }

        //System.out.println("token= "+jwt);


        if (jwt != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            String username = jwtUtil.extractUsername(jwt);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            //System.out.println("token1 ="+jwtUtil.validateToken(jwt));
            if (jwtUtil.validateToken(jwt)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        chain.doFilter(req, res);
    }
}

