package com.example.domainnamefinder.model;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SanType {
    DNS(2),
    IP(7);

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
