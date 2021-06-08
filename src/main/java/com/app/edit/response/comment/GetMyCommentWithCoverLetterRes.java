package com.app.edit.response.comment;

import com.app.edit.response.coverletter.GetCoverLettersByCommentRes;
import com.app.edit.response.coverletter.GetCoverLettersRes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class GetMyCommentWithCoverLetterRes {

    private final GetCoverLettersByCommentRes coverLetterRes;
    private final GetMyCommentsRes commentRes;
}
