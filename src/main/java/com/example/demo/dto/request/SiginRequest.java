package com.example.demo.dto.request;

import com.example.demo.infrastructure.util.Platform;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class SiginRequest {

    @NotBlank(message = "username must be not null")
    private String username;

    @NotBlank(message = "password must be not null")
    private String password;

    @NotNull(message = "platform must be not null")
    private Platform platform; // Thiết bị

    private String deviceToken; // Đăng nhập trên nhiều thiết bị mỗi thiết bị một cái token
}
