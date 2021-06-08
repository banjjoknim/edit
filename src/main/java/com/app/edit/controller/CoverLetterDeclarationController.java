package com.app.edit.controller;

import com.app.edit.config.BaseException;
import com.app.edit.config.BaseResponse;
import com.app.edit.config.BaseResponseStatus;
import com.app.edit.request.coverletter.PostCoverLetterDeclarationReq;
import com.app.edit.service.CoverLetterDeclarationService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequestMapping("/api")
@RestController
public class CoverLetterDeclarationController {

    private CoverLetterDeclarationService coverLetterDeclarationService;

    @Autowired
    public CoverLetterDeclarationController(CoverLetterDeclarationService coverLetterDeclarationService) {
        this.coverLetterDeclarationService = coverLetterDeclarationService;
    }

    /*
     * 자소서 신고 API
     **/
    @ApiOperation(value = "자소서 신고 API")
    @PostMapping("/declare-cover-letters")
    public BaseResponse<Long> postCoverLetterDeclaration(@RequestBody @Valid PostCoverLetterDeclarationReq request) throws BaseException {
        return new BaseResponse<>(BaseResponseStatus.SUCCESS,
                coverLetterDeclarationService.createCoverLetterDeclaration(request));
    }

    @ApiOperation(value = "자소서 신고 처리 API")
    @PatchMapping("/admin/cover-letter-declarations/{cover-letter-declaration-id}")
    public BaseResponse<Long> patchCoverLetterDeclaration(@PathVariable("cover-letter-declaration-id") Long coverLetterDeclarationId) throws BaseException {
        return new BaseResponse<>(BaseResponseStatus.SUCCESS,
                coverLetterDeclarationService.processCoverLetterDeclaration(coverLetterDeclarationId));
    }
}
