package com.example.demo.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
    String generatedToken(UserDetails userDetails);

    String extractToken(String token);

    String generatedRefreshToken(UserDetails userDetails);

    boolean validateToken(String token, UserDetails userDetails);
}
