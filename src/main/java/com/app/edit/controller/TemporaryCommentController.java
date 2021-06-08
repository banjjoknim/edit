package com.app.edit.controller;

import com.app.edit.config.BaseException;
import com.app.edit.config.BaseResponse;
import com.app.edit.provider.TemporaryCommentProvider;
import com.app.edit.request.temporarycomment.PostTemporaryCommentReq;
import com.app.edit.response.comment.GetMyCommentsRes;
import com.app.edit.response.temporaryComment.GetTemporaryCommentRes;
import com.app.edit.service.TemporaryCommentService;
import com.app.edit.utils.JwtService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.app.edit.config.BaseResponseStatus.EMPTY_USERID;
import static com.app.edit.config.BaseResponseStatus.SUCCESS;

@RequestMapping("/api")
@RestController
public class TemporaryCommentController {

    private final JwtService jwtService;
    private final TemporaryCommentProvider temporaryCommentProvider;
    private final TemporaryCommentService temporaryCommentService;

    @Autowired
    public TemporaryCommentController(JwtService jwtService,
                                      TemporaryCommentProvider temporaryCommentProvider,
                                      TemporaryCommentService temporaryCommentService){

        this.jwtService = jwtService;
        this.temporaryCommentProvider = temporaryCommentProvider;
        this.temporaryCommentService = temporaryCommentService;
    }


    @ApiOperation(value = "내 임시저장한 코멘트 조회")
    @GetMapping("/temporary-comments")
    public BaseResponse<List<GetMyCommentsRes>> getTemporaryComments() throws BaseException {

        try {
            Long userId = jwtService.getUserInfo().getUserId();

            if (userId == null || userId <= 0) {
                return new BaseResponse<>(EMPTY_USERID);
            }
            List<GetMyCommentsRes> getMyCommentsResList = temporaryCommentProvider.getMyTemporaryComments(userId);

            return new BaseResponse<>(SUCCESS, getMyCommentsResList);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ApiOperation(value = "코멘트 임시 저장하기")
    @PostMapping("/temporary-comments")
    public BaseResponse<Void> postTemporaryComments(
            @RequestBody PostTemporaryCommentReq parameters
    ) throws BaseException {

        try {
            Long userId = jwtService.getUserInfo().getUserId();

            if (userId == null || userId <= 0) {
                return new BaseResponse<>(EMPTY_USERID);
            }
            temporaryCommentService.createTemporaryComment(userId,parameters);

            return new BaseResponse<>(SUCCESS);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ApiOperation(value = "이어서 작성하기")
    @GetMapping("/temporary-comments/{temporary-comment-id}")
    public BaseResponse<GetTemporaryCommentRes> getTemporaryComment(

            @PathVariable("temporary-comment-id") Long temporaryCommentId) throws BaseException {

        try {
            Long userId = jwtService.getUserInfo().getUserId();

            if (userId == null || userId <= 0) {
                return new BaseResponse<>(EMPTY_USERID);
            }
            GetTemporaryCommentRes getTemporaryCommentRes =
                    temporaryCommentProvider.getMyTemporaryComment(temporaryCommentId,userId);

            return new BaseResponse<>(SUCCESS, getTemporaryCommentRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ApiOperation(value = "임시 코멘트 수정하기(이어서 작성하기 도중 다시 임시 저장할 때 사용)")
    @PatchMapping("/temporary-comments/{temporary-comment-id}")
    public BaseResponse<Void> updateTemporaryComment(

            @PathVariable("temporary-comment-id") Long temporaryCommentId,
            @RequestBody PostTemporaryCommentReq parameters) throws BaseException {

        try {

            temporaryCommentService.updateTemporaryComment(temporaryCommentId,parameters);

            return new BaseResponse<>(SUCCESS);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ApiOperation(value = "임시 코멘트 삭제하기")
    @DeleteMapping("/temporary-comments/{temporary-comment-id}")
    public BaseResponse<Void> deleteTemporaryComment(

            @PathVariable("temporary-comment-id") Long temporaryCommentId) throws BaseException {

        try {

            Long userId = jwtService.getUserInfo().getUserId();

            if (userId == null || userId <= 0)
                return new BaseResponse<>(EMPTY_USERID);


            temporaryCommentService.deleteTemporaryComment(temporaryCommentId, userId);

            return new BaseResponse<>(SUCCESS);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
