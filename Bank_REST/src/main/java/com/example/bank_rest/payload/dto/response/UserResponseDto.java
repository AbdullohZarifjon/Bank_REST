package com.example.bank_rest.payload.dto.response;

import com.example.bank_rest.entity.Role;
import com.example.bank_rest.entity.enums.UserStatus;
import lombok.Builder;

import java.util.List;

@Builder
public record UserResponseDto(
        Long id,
        String firstName,
        String lastName,
        String username,
        String phoneNumber,
        UserStatus userStatus,
        List<CardResponseDto> cardResponseDtoList,
        List<Role> roles) {

}
