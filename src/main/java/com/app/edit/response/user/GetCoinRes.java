package com.app.edit.response.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class GetCoinRes {

    private final Long coinCount;
    private final Long appreciateCount;
    private final Long adoptCount;
}
