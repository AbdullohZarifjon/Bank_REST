package com.example.bank_rest.service;

import com.example.bank_rest.entity.User;
import com.example.bank_rest.entity.enums.UserStatus;
import com.example.bank_rest.payload.dto.request.*;
import com.example.bank_rest.payload.dto.response.LoginResponseDto;
import com.example.bank_rest.payload.dto.response.UserCreateResponseDto;
import com.example.bank_rest.payload.dto.response.UserResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.data.domain.Page;

public interface UserService {

    UserCreateResponseDto register(UserRequestDto userRequestDto);

    LoginResponseDto signIn(LoginRequestDto loginDTO) throws JsonProcessingException;

    LoginResponseDto refreshToken(RefreshTokenDto dto) throws JsonProcessingException;

    UserResponseDto getUserById(Long userId);

    Page<UserCreateResponseDto> getAllUsers(int page, int size, Long id, String firstName);

    UserCreateResponseDto createUser(CreateUserForAdminRequestDto createUserForAdminRequestDto);

    UserResponseDto update(Long id, UpdateUserRequestDto updateUserRequestDto);

    void deactivateOwnAccount();

    UserResponseDto updateUserStatus(Long userId, UserStatus status);

    User getUserOrThrow(Long id);

    User getCurrentUserId();

    void validateUserStatus(User user);

}
