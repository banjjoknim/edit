package com.app.edit.response.sympathize;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class GetSympathizeCoverLetterRes {
    private final Long coverLetterId;
    private final String content;
    private final String coverLetterCategory;
    private final boolean Sympathy;
}
