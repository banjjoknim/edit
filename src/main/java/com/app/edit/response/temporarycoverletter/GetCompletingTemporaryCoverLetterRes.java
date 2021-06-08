package com.app.edit.response.temporarycoverletter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class GetCompletingTemporaryCoverLetterRes {

    private Long temporaryCoverLetterId;
    private Long originalCoverLetterCategoryId;
    private String originalCoverLetterContent;
    private String adoptedCommentContent;
    private String temporaryCoverLetterContent;
}
