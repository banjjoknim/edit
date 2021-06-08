package com.app.edit.response.coverletter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class GetCoverLetterToCompleteRes {

    private Long originalCoverLetterId;
    private String originalCoverLetterCategoryName;
    private String originalCoverLetterContent;
    private String adoptedCommentContent;
}
