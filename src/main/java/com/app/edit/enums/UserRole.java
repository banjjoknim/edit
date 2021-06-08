package com.app.edit.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum UserRole {
    MENTEE,
    MENTOR,
    ADMIN;

    public static UserRole dataToEnum(String dbData) {
        return Arrays.stream(UserRole.values())
                .filter(userRole -> userRole.name().equals(dbData))
                .findAny()
                //TODO 임시 예외 처리
                .orElse(MENTEE);
    }
}
