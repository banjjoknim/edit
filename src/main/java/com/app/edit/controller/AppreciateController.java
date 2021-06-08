package com.app.edit.controller;

import com.app.edit.config.BaseException;
import com.app.edit.config.BaseResponse;
import com.app.edit.config.BaseResponseStatus;
import com.app.edit.domain.appreciate.AppreciateId;
import com.app.edit.service.AppreciateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api")
@RestController
public class AppreciateController {

    private final AppreciateService appreciateService;

    @Autowired
    public AppreciateController(AppreciateService appreciateService) {
        this.appreciateService = appreciateService;
    }

    /*
     * 코멘트에 감사하기 API
     **/
    @PatchMapping("/comments/{comment-id}/appreciate-comments")
    public BaseResponse<AppreciateId> patchAppreciateComment(@PathVariable("comment-id") Long commentId) throws BaseException {
        return new BaseResponse<>(BaseResponseStatus.SUCCESS, appreciateService.createOrUpdateAppreciate(commentId));
    }
}
