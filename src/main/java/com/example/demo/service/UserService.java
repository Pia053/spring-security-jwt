package com.example.demo.service;

import com.example.demo.dto.request.UserRequestDTO;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.dto.response.UserDetailResponse;
import com.example.demo.infrastructure.util.UserStatus;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;


// Tính đóng gói, tính kế thừa


public interface UserService {

    UserDetailsService userDetailsService();

    long saveUser(UserRequestDTO userRequestDTO);

    void updateUser(long userId, UserRequestDTO userRequestDTO);

    void changeStatus(long userId, UserStatus status);

    void deleteUser(long userId);

    UserDetailResponse getUser(long userId);

    PageResponse<?>  getAllUserWithSortBy(int pageNo, int pageSize, String sortBy);

    PageResponse<?> getAllUserWithSortByMultipleColumns(int pageNo, int pageSize, List<String> sortBy);

    PageResponse<?> getAllUserWithSortByMultipleColumnsAndSearch(int pageNo, int pageSize, String sortBy, String search);

    PageResponse<?> advanceSearchByCriteria(int pageNo, int pageSize, String sortBy, List<String> search, String address);
}
