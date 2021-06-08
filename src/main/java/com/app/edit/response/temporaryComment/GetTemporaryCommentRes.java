package com.app.edit.response.temporaryComment;

import com.app.edit.response.comment.GetMyCommentRes;
import com.app.edit.response.comment.GetMyCommentWithCoverLetterRes;
import com.app.edit.response.coverletter.GetCoverLettersByCommentRes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class GetTemporaryCommentRes {

    private final GetCoverLettersByCommentRes getCoverLettersByTemporaryCommentRes;
    private final GetMyCommentRes getMyTemporaryCommentRes;
}
