package com.app.edit.response.temporarycoverletter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class GetWritingTemporaryCoverLetterRes {

    private Long temporaryCoverLetterId;
    private Long coverLetterCategoryId;
    private String content;
}
