package com.app.edit.response.comment;

import com.app.edit.response.coverletter.GetCoverLettersRes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class GetCommentsRes {

    GetCoverLettersRes coverLetterInfo;
    List<CommentInfo> commentInfos;
}
