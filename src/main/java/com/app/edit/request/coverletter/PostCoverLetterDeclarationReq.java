package com.app.edit.request.coverletter;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@NoArgsConstructor
@Getter
@Setter
public class PostCoverLetterDeclarationReq {

    @NotNull(message = "자소서 ID를 입력해주세요.")
    private Long coverLetterId;
}
