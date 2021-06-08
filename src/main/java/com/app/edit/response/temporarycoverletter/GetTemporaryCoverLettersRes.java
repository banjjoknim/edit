package com.app.edit.response.temporarycoverletter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class GetTemporaryCoverLettersRes {

    private Long temporaryCoverLetterId;
    private String nickName;
    private String jobName;
    private String coverLetterCategoryName;
    private String temporaryCoverLetterContent;
}
