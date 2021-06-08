package com.app.edit.response.sympathize;

import com.app.edit.response.user.GetSympathizeUserRes;
import com.app.edit.response.user.GetUserInfosRes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class GetSympathizeCoverLettersRes {

    private final Long id;
    private final GetUserInfosRes getSympathizeUserRes;
    private final GetSympathizeCoverLetterRes getSympathizeCoverLetterRes;



}
