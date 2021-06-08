package com.app.edit.request.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class PatchSearchPasswordReq {
    private String email;
    private String phoneNumber;
    private String name;
}
