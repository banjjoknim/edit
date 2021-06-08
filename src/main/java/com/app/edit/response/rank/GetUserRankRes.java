package com.app.edit.response.rank;

import com.app.edit.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class GetUserRankRes {

    private final Long rankId;
    private final Long userId;
    private final String nickName;
    private final String emotionName;
    private final String colorName;
    private final String jobName;
}
