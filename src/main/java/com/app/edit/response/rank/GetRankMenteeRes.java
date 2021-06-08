package com.app.edit.response.rank;

import com.app.edit.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class GetRankMenteeRes {
    private final Long rankId;
    private final String nickName;
    private final String emotionName;
    private final String colorName;
    private final UserRole userRole;
    private final Long coverLetterCount;
    private final Long coverLetterCompleteCount;
}
