package com.app.edit.request.comment;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class PostCommentDeclarationReq {

    @NotNull(message = "코멘트 ID를 입력해주세요.")
    private Long commentId;
}
