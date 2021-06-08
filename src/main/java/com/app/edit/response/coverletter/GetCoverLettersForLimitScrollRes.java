package com.app.edit.response.coverletter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class GetCoverLettersForLimitScrollRes {

    private List<GetCoverLettersRes> coverLetters;
    private Long totalCoverLetterCount;
    private Boolean hasNext;
}
