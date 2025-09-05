package com.example.bank_rest.payload.dto.response;

import com.example.bank_rest.entity.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserCreateResponseDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String phoneNumber;
    private UserStatus userStatus;

    public UserCreateResponseDto(long id, String firstName, String lastName, String username, String phoneNumber) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.phoneNumber = phoneNumber;
    }
}
