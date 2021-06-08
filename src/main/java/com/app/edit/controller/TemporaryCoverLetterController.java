package com.app.edit.controller;

import com.app.edit.config.BaseException;
import com.app.edit.config.BaseResponse;
import com.app.edit.config.BaseResponseStatus;
import com.app.edit.enums.CoverLetterType;
import com.app.edit.provider.TemporaryCoverLetterProvider;
import com.app.edit.request.temporarycoverletter.*;
import com.app.edit.response.temporarycoverletter.GetCompletingTemporaryCoverLetterRes;
import com.app.edit.response.temporarycoverletter.GetTemporaryCoverLettersRes;
import com.app.edit.response.temporarycoverletter.GetWritingTemporaryCoverLetterRes;
import com.app.edit.service.TemporaryCoverLetterService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.app.edit.config.Constant.DEFAULT_PAGE_SIZE;

@RequestMapping("/api")
@RestController
public class TemporaryCoverLetterController {

    private final TemporaryCoverLetterService temporaryCoverLetterService;
    private final TemporaryCoverLetterProvider temporaryCoverLetterProvider;

    @Autowired
    public TemporaryCoverLetterController(TemporaryCoverLetterService temporaryCoverLetterService, TemporaryCoverLetterProvider temporaryCoverLetterProvider) {
        this.temporaryCoverLetterService = temporaryCoverLetterService;
        this.temporaryCoverLetterProvider = temporaryCoverLetterProvider;
    }

    /*
     * 작성중인 자소서 임시 저장 API
     **/
    @ApiOperation(value = "작성중인 자소서 임시 저장 API")
    @PostMapping("/writing-temporary-cover-letters")
    public BaseResponse<Long> postWritingTemporaryCoverLetter(@RequestBody @Valid PostWritingTemporaryCoverLetterReq request) throws BaseException {
        return new BaseResponse<>(BaseResponseStatus.SUCCESS,
                temporaryCoverLetterService.createWritingTemporaryCoverLetter(request));
    }

    /*
     * 완성중인 자소서 임시 저장 API
     **/
    @ApiOperation(value = "완성중인 자소서 임시 저장 API")
    @PostMapping("/completing-temporary-cover-letters")
    public BaseResponse<Long> postCompletingTemporaryCoverLetter(@RequestBody @Valid PostCompletingTemporaryCoverLetterReq request) throws BaseException {
        return new BaseResponse<>(BaseResponseStatus.SUCCESS,
                temporaryCoverLetterService.createCompletingTemporaryCoverLetter(request));
    }

    /**
     * 임시 저장한 작성중/완성중인 자소서 목록 조회 API
     *
     * @param page
     * @param coverLetterType
     * @return
     * @throws BaseException
     */
    @ApiOperation(value = "임시 저장한 작성중/완성중인 자소서 목록 조회 API")
    @GetMapping("/temporary-cover-letters")
    public BaseResponse<List<GetTemporaryCoverLettersRes>> getTemporaryCoverLetters(@RequestParam Integer page, @RequestParam CoverLetterType coverLetterType) throws BaseException {
        PageRequest pageRequest = com.app.edit.config.PageRequest.of(page, DEFAULT_PAGE_SIZE);
        return new BaseResponse<>(BaseResponseStatus.SUCCESS,
                temporaryCoverLetterProvider.retrieveTemporaryCoverLetters(pageRequest, coverLetterType));
    }

    /**
     * 임시 저장한 작성중인 자소서 이어서 작성하기(수정하기) API
     *
     * @param temporaryCoverLetterId
     * @param request
     * @return
     * @throws BaseException
     */
    @ApiOperation(value = "임시 저장한 작성중인 자소서 이어서 작성하기(수정하기) API")
    @PatchMapping("/writing-temporary-cover-letters/{temporary-cover-letter-id}")
    public BaseResponse<Long> patchWritingTemporaryCoverLetter(@PathVariable("temporary-cover-letter-id") Long temporaryCoverLetterId,
                                                               @RequestBody @Valid PatchWritingTemporaryCoverLetterReq request) throws BaseException {
        return new BaseResponse<>(BaseResponseStatus.SUCCESS,
                temporaryCoverLetterService.updateWritingTemporaryCoverLetterById(temporaryCoverLetterId, request));
    }

    @ApiOperation(value = "임시 저장한 완성중인 자소서 이어서 작성하기(수정하기) API")
    @PatchMapping("/completing-temporary-cover-letters/{temporary-cover-letter-id}")
    public BaseResponse<Long> patchCompletingTemporaryCoverLetter(@PathVariable("temporary-cover-letter-id") Long temporaryCoverLetterId,
                                                                  @RequestBody @Valid PatchCompletingTemporaryCoverLetterReq request) throws BaseException {
        return new BaseResponse<>(BaseResponseStatus.SUCCESS,
                temporaryCoverLetterService.updateCompletingTemporaryCoverLetterById(temporaryCoverLetterId, request));
    }

    /**
     * 임시 저장한 작성중인 자소서 조회 API
     * @param temporaryCoverLetterId
     * @return
     * @throws BaseException
     */
    @ApiOperation(value = "임시 저장한 작성중인 자소서 조회 API")
    @GetMapping("/writing-temporary-cover-letters/{temporary-cover-letter-id}")
    public BaseResponse<GetWritingTemporaryCoverLetterRes> getWritingTemporaryCoverLetter(@PathVariable("temporary-cover-letter-id") Long temporaryCoverLetterId) throws BaseException {
        return new BaseResponse<>(BaseResponseStatus.SUCCESS,
                temporaryCoverLetterProvider.retrieveWritingTemporaryCoverLetter(temporaryCoverLetterId));
    }

    /**
     * 임시 저장한 완성중인 자소서 조회 API
     * @param temporaryCoverLetterId
     * @return
     * @throws BaseException
     */
    @ApiOperation(value = "임시 저장한 완성중인 자소서 조회 API")
    @GetMapping("/completing-temporary-cover-letters/{temporary-cover-letter-id}")
    public BaseResponse<GetCompletingTemporaryCoverLetterRes> getCompletingTemporaryCoverLetter(@PathVariable("temporary-cover-letter-id") Long temporaryCoverLetterId) throws BaseException {
        return new BaseResponse<>(BaseResponseStatus.SUCCESS,
                temporaryCoverLetterProvider.retrieveCompletingTemporaryCoverLetter(temporaryCoverLetterId));
    }

    @ApiOperation(value = "임시 저장한 작성중인 자소서 자소서로 등록 API")
    @PostMapping("/converted-writing-cover-letter")
    public BaseResponse<Long> postWritingCoverLetterFromTemporary(@RequestBody @Valid PostWritingCoverLetterFromTemporaryReq request) throws BaseException {
        return new BaseResponse<>(BaseResponseStatus.SUCCESS, temporaryCoverLetterService.createWritingCoverLetterFromTemporary(request));
    }

    @ApiOperation(value = "임시 저장한 완성중인 자소서 완성한 자소서로 등록 API")
    @PostMapping("/converted-completing-cover-letter")
    public BaseResponse<Long> postCompletingCoverLetterFromTemporary(@RequestBody @Valid PostCompletingCoverLetterFromTemporaryReq request) throws BaseException {
        return new BaseResponse<>(BaseResponseStatus.SUCCESS, temporaryCoverLetterService.createCompletingCoverLetterFromTemporary(request));
    }
}


