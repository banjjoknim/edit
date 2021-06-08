package com.app.edit.request.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@Getter
@Setter
public class PatchRoleReq {

    @NotBlank(message = "역할 변경 사유를 입력해주세요.")
    private String changeContent;

    private String etcChangeContent;

}
