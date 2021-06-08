package com.app.edit.response.comment;

import com.app.edit.enums.IsAdopted;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class CommentInfo {

    private Long commentId;
    private String userProfile;
    private String nickName;
    private String jobName;
    private String sentenceEvaluation;
    private String concretenessLogic;
    private String sincerity;
    private String activity;
    private String commentContent;
    private IsAdopted isAdopted;
    private Boolean isMine;
    private Boolean isAppreciated;
}
