package com.domainname.finder.model;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SanType {
    OTHER_NAME(0),
    RFC_822_NAME(1),
    DNS_NAME(2),
    X_400_ADDRESS(3),
    DIRECTORY_NAME(4),
    EDI_PARTY_NAME(5),
    UNIFORM_RESOURCE_IDENTIFIER(6),
    IP_ADDRESS(7),
    REGISTERED_ID(8);

    private final int code;

    public static SanType of(int code) {
        for (SanType value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        return null;
    }
}
