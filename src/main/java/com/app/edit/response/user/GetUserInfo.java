package com.app.edit.response.user;

import com.app.edit.enums.State;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class GetUserInfo {

    private final Long userId;
    private final String role;
}
