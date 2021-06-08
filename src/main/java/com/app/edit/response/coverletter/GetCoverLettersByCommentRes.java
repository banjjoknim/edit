package com.app.edit.response.coverletter;

import com.app.edit.response.user.GetUserInfosRes;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Builder
public class GetCoverLettersByCommentRes {

    private final GetUserInfosRes userInfo;
    private final Long coverLetterId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String coverLetterCategoryName;
    private final String coverLetterContent;
}
