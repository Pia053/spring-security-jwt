package com.example.demo.dto.request;

import com.example.demo.infrastructure.util.EnumPattern;
import com.example.demo.infrastructure.util.EnumValue;
import com.example.demo.infrastructure.util.Gender;
import com.example.demo.infrastructure.util.GenderSubset;
import com.example.demo.infrastructure.util.PhoneNumber;
import com.example.demo.infrastructure.util.UserStatus;
import com.example.demo.infrastructure.util.UserType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;


// Null: Perpose: Đảm bảo trường chú thích không rỗng
// Blank: Không rỗng hoặc không trắng (ít nhất một ký tự không phải trắng)
// Empty: Chuỗi, mảng kông rỗng và không trống

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateUserRequest implements Serializable {
    @NotBlank(message = "First name not blank")
    private String firstName;

    @NotNull(message = "Last name not null")
    private String lastName;

    @Email(message = "Email invalid format")
    private String email;

    //    @Pattern(regexp = "^\\d{10}$", message = "phone invalid format")
    @PhoneNumber
    private String phone;

    @NotNull(message = "dateOfBirth must be not null")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "MM/dd/yyyy")
    private Date dateOfBirth;

    @NotNull(message = "username must be not null")
    private String username;

    @NotNull(message = "password must be not null")
    private String password;

    @EnumPattern(name = "status", regexp = "ACTIVE|INACTIVE|NONE")
    private UserStatus status;

    @GenderSubset(anyOf = {
            Gender.MALE,
            Gender.FEMALE,
            Gender.OTHER
    })
    private Gender gender;


    @NotNull(message = "type must be not null")
    @EnumValue(name = "type", enumClass = UserType.class)
    private String type;

    @NotEmpty(message = "addresses can not empty")
    private Set<Address> addresses;


    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class Address {
        private String apartmentNumber;
        private String floor;
        private String building;
        private String streetNumber;
        private String street;
        private String city;
        private String country;
        private Integer addressType;
    }


}
