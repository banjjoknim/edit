package com.app.edit.request.user;

import com.app.edit.enums.UserRole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@NoArgsConstructor
@Getter
@Setter
public class DeleteUserReq {


    private String withdrawalContent;

    @NotBlank(message = "최소 10자이상 입력해주세요.")
    @Size(max = 100, message = "기타 의견은 최대 100자 입니다.")
    private String etcWithdrawalContent;
}
