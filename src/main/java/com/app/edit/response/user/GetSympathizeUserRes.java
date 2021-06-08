package com.app.edit.response.user;

import com.app.edit.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class GetSympathizeUserRes {

    private final String name;
    private final String emotionName;
    private final String colorName;
    private final UserRole userRole;
    private final String jobName;
}
