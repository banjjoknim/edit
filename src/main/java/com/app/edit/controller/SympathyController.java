package com.app.edit.controller;

import com.app.edit.config.BaseException;
import com.app.edit.config.BaseResponse;
import com.app.edit.config.BaseResponseStatus;
import com.app.edit.domain.sympathy.Sympathy;
import com.app.edit.domain.sympathy.SympathyId;
import com.app.edit.service.SympathyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api")
@RestController
public class SympathyController {

    private final SympathyService sympathyService;

    @Autowired
    public SympathyController(SympathyService sympathyService) {
        this.sympathyService = sympathyService;
    }


    /**
     * 자소서에 공감하기 API
     * @param coverLetterId
     * @return
     */
    @ApiOperation(value = "자소서에 공감하기 API")
    @PatchMapping("/cover-letters/{cover-letter-id}/sympathize-cover-letters")
    public BaseResponse<SympathyId> patchSympathizeCoverLetterById(@PathVariable("cover-letter-id") Long coverLetterId) throws BaseException {
        return new BaseResponse<>(BaseResponseStatus.SUCCESS, sympathyService.createOrUpdateSympathy(coverLetterId));
    }
}
