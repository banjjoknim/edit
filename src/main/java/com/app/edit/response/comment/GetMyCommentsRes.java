package com.app.edit.response.comment;

import com.app.edit.response.user.GetUserInfosRes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class GetMyCommentsRes {

    private final GetUserInfosRes userInfo;
    private final GetMyCommentRes commentInfo;
}
