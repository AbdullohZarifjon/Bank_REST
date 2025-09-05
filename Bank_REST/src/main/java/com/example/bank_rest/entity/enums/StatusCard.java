package com.example.bank_rest.entity.enums;

public enum StatusCard {
    ACTIVE,
    BLOCKED,
    EXPIRED,
    PENDING_BLOCK,
    DELETED;

    public boolean isUsable() {
        return this == ACTIVE;
    }
}
