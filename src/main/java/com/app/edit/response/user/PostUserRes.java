package com.app.edit.response.user;

import com.app.edit.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Builder
@AllArgsConstructor
public class PostUserRes {
    private final String jwt;
    private final UserRole userRole;
    private Boolean isCertificatedMentor;
}
