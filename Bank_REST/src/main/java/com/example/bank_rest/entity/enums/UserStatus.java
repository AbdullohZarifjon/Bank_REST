package com.example.bank_rest.entity.enums;

public enum UserStatus {
    ACTIVE,
    INACTIVE,
    BLOCKED;

    public boolean isUsable() {
        return this == ACTIVE;
    }
}