package com.app.edit.response.coverletter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class GetMainCoverLettersRes {

    List<GetCoverLettersRes> todayCoverLetters;
    List<GetCoverLettersRes> waitingForCommentCoverLetters;
    List<GetCoverLettersRes> adoptedCoverLetters;
    List<GetCoverLettersRes> sympathiesCoverLetters;
}
