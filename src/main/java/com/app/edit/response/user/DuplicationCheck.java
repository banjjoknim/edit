package com.app.edit.response.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class DuplicationCheck {
    private final String duplicationCheck;
}
