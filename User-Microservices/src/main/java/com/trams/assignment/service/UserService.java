package com.trams.assignment.service;

import java.util.List;

import com.trams.assignment.dto.LoginRequest;
import com.trams.assignment.dto.LoginResponse;
import com.trams.assignment.dto.UserRequest;
import com.trams.assignment.dto.UserResponse;

public interface UserService {

    UserResponse registerUser(UserRequest request);

    LoginResponse loginUser(LoginRequest request);

    UserResponse getUserById(Long id);

    List<UserResponse> getAllUsers();

    UserResponse updateUser(Long id, UserRequest request);

    void deleteUser(Long id);
}