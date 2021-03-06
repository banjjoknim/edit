package com.app.edit.request.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class UpdatePasswordReq {
    private String password;
    private String authenticationPassword;
}
