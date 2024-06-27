package com.example.demo.controller;

import com.example.demo.dto.request.UserRequestDTO;
import com.example.demo.dto.response.ResponseData;
import com.example.demo.dto.response.ResponseError;
import com.example.demo.dto.response.UserDetailResponse;
import com.example.demo.infrastructure.util.UserStatus;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
@Validated
@Slf4j
@Tag(name = "User Controller")
public class UserController {

    @Autowired
    private UserService userService;


    @GetMapping("/{userId}")
    public ResponseData<?> getUser(@Min(1) @PathVariable(name = "userId") Integer id) {
        log.info("User id = {}", id);
        UserDetailResponse userDetailResponse = userService.getUser(id);
        try {
            return new ResponseData<>(
                    HttpStatus.OK.value(),
                    "Request get user detail successfully",
                    userDetailResponse
            );
        } catch (Exception e) {
            return new ResponseData<>(
                    HttpStatus.OK.value(), "Request get user detail Fail", e.getMessage() + " - " + e.getCause()
            );
        }

    }


    @GetMapping("/list")
    public ResponseData<?> getAllUser(
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "1", required = false) int pageNo,
            @Min(5) @RequestParam(defaultValue = "10", required = false) int pageSize

    ) {
        log.info("{}", pageSize);
        return new ResponseData(
                HttpStatus.OK.value(),
                "Request get user detail successfully",
                userService.getAllUserWithSortBy(pageNo, pageSize, sortBy)
        );

    }


    @GetMapping("/list-sort-by-multiple-columns")
    public ResponseData<?> getAllUserWithSortByMultipleColumns(
            @RequestParam(required = false) List<String> sortBy,
            @RequestParam(defaultValue = "1", required = false) int pageNo,
            @Min(5) @RequestParam(defaultValue = "10", required = false) int pageSize

    ) {
        log.info("Request get all list of users with sort by multiple columns");
        return new ResponseData(
                HttpStatus.OK.value(),
                "Request get user detail successfully",
                userService.getAllUserWithSortByMultipleColumns(pageNo, pageSize, sortBy)
        );

    }


    @GetMapping("/list-sort-by-multiple-columns-search")
    public ResponseData<?> getAllUserWithSortByMultipleColumnsAndSearch(
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "1", required = false) int pageNo,
            @Min(5) @RequestParam(defaultValue = "10", required = false) int pageSize

    ) {
        log.info("Request get all list of users with sort by multiple columns and Search");
        return new ResponseData(
                HttpStatus.OK.value(),
                "Request get user detail successfully",
                userService.getAllUserWithSortByMultipleColumnsAndSearch(pageNo, pageSize, sortBy, search)
        );

    }

    @GetMapping("/advance-search-by-criteria")
    public ResponseData<?> advanceSearchByCriteria(
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "") List<String> search,
            @RequestParam(defaultValue = "1", required = false) int pageNo,
            @Min(5) @RequestParam(defaultValue = "10", required = false) int pageSize

    ) {
        log.info("Request get all list of users with sort by multiple columns and Search");
        return new ResponseData(
                HttpStatus.OK.value(),
                "Request get user detail successfully",
                userService.advanceSearchByCriteria(pageNo, pageSize, sortBy, search, address)
        );
    }

    @Operation(summary = "Create User", description = "Create new user")
    @PostMapping(value = "/creat", headers = "apiKey=v1.0")
    public ResponseData<?> createUser(@Valid @RequestBody UserRequestDTO createUserRequest) {
        try {
            long userId = userService.saveUser(createUserRequest);
            return new ResponseData<>(HttpStatus.CREATED.value(), "User added successfully", userId);
        } catch (Exception e) {
            log.error("ERROR MESSAGE = {}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Save User fail");
        }
    }

    @Operation(
            summary = "Update an user",
            description = "Update an existing employee. " +
                    "The response is updated Employee object with id, " +
                    "first name, and last name."
    )
    @PutMapping("/{userId}")
    public ResponseData<?> updateUser(
            @Valid @RequestBody UserRequestDTO createUserRequest,
            @PathVariable(name = "userId") Integer id
    ) {
        log.info("User id = {} {}", id, createUserRequest.getFirstName());
        try {
            userService.updateUser(id, createUserRequest);
            return new ResponseData<>(HttpStatus.ACCEPTED.value(), "User update successfully");
        } catch (Exception e) {
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Update user fail");
        }
    }

    @PatchMapping("/{userId}")
    public ResponseData<?> changeStatus(
            @RequestParam(required = false) UserStatus status,
            @PathVariable(name = "userId") Integer id
    ) {
        log.info("Request change status, userId={}", id);

        try {
            userService.changeStatus(id, status);
            return new ResponseData<>(HttpStatus.ACCEPTED.value(), "Change status user successfully");
        } catch (Exception e) {
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Change status fail");
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseData<?> deleteUser(
            @PathVariable(name = "userId") Integer id
    ) {
        log.info("Request delete userId={}", id);

        try {
            userService.deleteUser(id);
            return new ResponseData<>(HttpStatus.NO_CONTENT.value(), "Delete user successfully");
        } catch (Exception e) {
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Delete user fail");
        }
    }


}
