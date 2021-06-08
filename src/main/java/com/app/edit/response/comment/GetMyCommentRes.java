package com.app.edit.response.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Builder
public class GetMyCommentRes {

    private final Long commentId;
    private final String sentenceEvaluation;
    private final String concretenessLogic;
    private final String sincerity;
    private final String activity;
    private final String commentContent;

}
