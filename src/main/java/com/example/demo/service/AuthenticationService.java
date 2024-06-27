package com.example.demo.service;

import com.example.demo.dto.request.SiginRequest;
import com.example.demo.dto.response.TokenResponse;

public interface AuthenticationService {
    TokenResponse authenticate(SiginRequest request);
}
