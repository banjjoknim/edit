package com.app.edit.request.comment;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PostCommentReq {

    private Long coverLetterId;
    private String sentenceEvaluation;
    private String activity;
    private String sincerity;
    private String concretenessLogic;
    private String content;
}
