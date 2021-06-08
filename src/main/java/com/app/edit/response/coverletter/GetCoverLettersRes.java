package com.app.edit.response.coverletter;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@Getter
@Setter
public class GetCoverLettersRes {

    private Long coverLetterId;
    private String userProfile;
    private String nickName;
    private String jobName;
    private String coverLetterCategoryName;
    private String coverLetterContent;
    private String completedCoverLetterContent;
    private Boolean isSympathy;
    private Long sympathiesCount;
    private Boolean isMine;
}
