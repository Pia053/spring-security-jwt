package com.example.demo.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Builder
public class UserDetailResponse implements Serializable {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;

    public UserDetailResponse(Long id, String firstName, String lastName, String email, String phone) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
    }
}
