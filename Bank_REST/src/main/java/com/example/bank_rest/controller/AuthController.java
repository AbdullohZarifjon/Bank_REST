package com.example.bank_rest.controller;

import com.example.bank_rest.payload.common.ApiResponse;
import com.example.bank_rest.payload.common.ApiResponseFactory;
import com.example.bank_rest.payload.dto.request.LoginRequestDto;
import com.example.bank_rest.payload.dto.request.RefreshTokenDto;
import com.example.bank_rest.payload.dto.request.UserRequestDto;
import com.example.bank_rest.payload.dto.response.LoginResponseDto;
import com.example.bank_rest.payload.dto.response.UserCreateResponseDto;
import com.example.bank_rest.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/sign-up")
    public ApiResponse<UserCreateResponseDto> signUp(@RequestBody @Valid UserRequestDto userRequestDto) {
        UserCreateResponseDto createdUser = userService.register(userRequestDto);
        return ApiResponseFactory.created("User successfully registered", createdUser);
    }

    @PostMapping("/sign-in")
    public ApiResponse<LoginResponseDto> signIn(@RequestBody @Valid LoginRequestDto loginDTO)
            throws JsonProcessingException {
        LoginResponseDto tokens = userService.signIn(loginDTO);
        return ApiResponseFactory.success("Successfully signed in", tokens);
    }

    @PostMapping("/refresh-token")
    public ApiResponse<LoginResponseDto> refreshToken(@RequestBody @Valid RefreshTokenDto dto)
            throws JsonProcessingException {
        LoginResponseDto newTokens = userService.refreshToken(dto);
        return ApiResponseFactory.success("Token successfully refreshed", newTokens);
    }

    @PostMapping("/sign-out")
    public ApiResponse<Void> signOut() {
        return ApiResponseFactory.noContent("Successfully signed out");
    }
}
