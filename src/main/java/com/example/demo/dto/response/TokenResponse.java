package com.example.demo.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TokenResponse {
    private String accessToken; // truy cập

    private String refreshToken; // làm mới token sau 1 tiếng tự động cập nhập bên máy khách

    private Long userId;

//    more over
}
