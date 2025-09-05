package com.example.bank_rest.controller;

import com.example.bank_rest.entity.enums.UserStatus;
import com.example.bank_rest.payload.common.ApiResponse;
import com.example.bank_rest.payload.common.ApiResponseFactory;
import com.example.bank_rest.payload.dto.request.CreateUserForAdminRequestDto;
import com.example.bank_rest.payload.dto.request.UpdateUserRequestDto;
import com.example.bank_rest.payload.dto.response.UserCreateResponseDto;
import com.example.bank_rest.payload.dto.response.UserResponseDto;
import com.example.bank_rest.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/users")
@Tag(name = "User Controller", description = "APIs for managing users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Admin: Create a new user")
    public ApiResponse<UserCreateResponseDto> createUser(@RequestBody @Valid CreateUserForAdminRequestDto createUserForAdminRequestDto) {
        return ApiResponseFactory.created("User created successfully", userService.createUser(createUserForAdminRequestDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID (Admin or Self)")
    @PreAuthorize("hasRole('ROLE_ADMIN') or #id == principal.id")
    public ApiResponse<UserResponseDto> getUserById(@PathVariable @Min(1) Long id) {
        return ApiResponseFactory.success("User found", userService.getUserById(id));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get all users with pagination and optional filters (Admin only)")
    public ApiResponse<Page<UserCreateResponseDto>> getAllUsersForAdmin(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String firstName
    ) {
        return ApiResponseFactory.success(
                "Users list",
                userService.getAllUsers(page, size, id, firstName)
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or #id == principal.id")
    @Operation(summary = "Update a user (Admin or Self)")
    public ApiResponse<UserResponseDto> update(
            @PathVariable @Min(1) Long id,
            @RequestBody @Valid UpdateUserRequestDto updateUserRequestDto) {
        return ApiResponseFactory.success("User updated", userService.update(id, updateUserRequestDto));
    }

    @PutMapping("/me/deactivate")
    @Operation(summary = "User: Deactivate own account")
    public ApiResponse<Void> deactivateOwnAccount() {
        userService.deactivateOwnAccount();
        return ApiResponseFactory.success("Your account has been deactivated");
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Admin: Update user status")
    public ApiResponse<UserResponseDto> updateUserStatus(
            @PathVariable Long id,
            @RequestParam UserStatus status
    ) {
        return ApiResponseFactory.success(
                "User status updated",
                userService.updateUserStatus(id, status)
        );
    }
}
