package com.example.demo.service.impl;

import com.example.demo.dto.request.SiginRequest;
import com.example.demo.dto.response.TokenResponse;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.AuthenticationService;
import com.example.demo.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Override
    public TokenResponse authenticate(SiginRequest request) {
//        verify xác thực xem trùng không có
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );


        var user = userRepository.findByUsername(request.getUsername()).orElseThrow(
                () -> new UsernameNotFoundException("Username not found")
        );

        String access_token = jwtService.generatedToken(user);

        String refreshToken = jwtService.generatedRefreshToken(user);
        return TokenResponse.builder()
                .accessToken(access_token)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .build();
    }
}
