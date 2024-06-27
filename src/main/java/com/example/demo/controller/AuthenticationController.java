package com.example.demo.controller;

import com.example.demo.dto.request.SiginRequest;
import com.example.demo.dto.response.TokenResponse;
import com.example.demo.model.User;
import com.example.demo.service.AuthenticationService;
import com.example.demo.service.JwtService;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Validated
@Slf4j
@Tag(name = "Authentication Controller")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody SiginRequest request){
        return new ResponseEntity<>(
                authenticationService.authenticate(request), HttpStatus.OK
        );
    }

    @PostMapping("/refresh")
    public  String refresh(String username, String password){
        return "success";
    }

    @PostMapping("/logout")
    public  String logout(String username, String password){
        return "success";
    }


}
