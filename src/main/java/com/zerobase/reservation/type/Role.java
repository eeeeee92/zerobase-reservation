package com.zerobase.reservation.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {

    GUEST("ROLE_GUEST"),
    USER("ROLE_USER"),
    SELLER("ROLE_SELLER"),
    ADMIN("ROLE_ADMIN");

    private final String key;
}
