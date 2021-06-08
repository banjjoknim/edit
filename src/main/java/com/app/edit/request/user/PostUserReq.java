package com.app.edit.request.user;

import com.app.edit.enums.UserRole;
import lombok.*;

@NoArgsConstructor
@Getter
@Setter
public class PostUserReq {
    private String name;
    private String nickname;
    private String jobName;
    private String email;
    private String etcJobName;
    private String phoneNumber;
    private String password;
    private String authenticationPassword;
    private UserRole userRole;
}
