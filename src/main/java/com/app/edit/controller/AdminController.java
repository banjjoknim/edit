package com.app.edit.controller;

import com.app.edit.config.BaseException;
import com.app.edit.config.BaseResponse;
import com.app.edit.service.AdminService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.app.edit.config.BaseResponseStatus.SUCCESS;

@Slf4j
@RequestMapping("/api")
@RestController
public class AdminController {

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    /**
     * 관리자 멘토 인증 처리
     * @param userId
     * @param
     * @return
     */
    @PatchMapping("/admin/authentication-mentors/{userId}")
    @ApiOperation(value = "멘토 인증 처리",notes = "멘토 인증 처리"+"\n" +
            "changeType = 1 -> 인증 승낙, changeType = 2 ->  인증 거절")
    public BaseResponse<Void> authenticationMentors(
            @PathVariable Long userId,
            @RequestParam(value = "authenticationType") Integer authenticationType ){

        try {
            adminService.manageAuthentication(userId, authenticationType);
            return new BaseResponse<>(SUCCESS);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

//    /**
//     * 관리자 멘티 -> 멘토 역할 변경 처리
//     * @param userId
//     * @param changeType
//     * @return
//     */
//    @PatchMapping("/admin/mentee-to-mentors/{userId}")
//    @ApiOperation(value = "멘토 인증 처리(미완성)",notes = "멘토 인증 처리(미완성)"+"\n" +
//            "changeType = 1 -> (멘티 -> 멘토), changeType = 2 -> 역할 변경 거절")
//    public BaseResponse<Void> changeRoleMenteeToMentors(
//            @PathVariable Long userId,
//            @RequestParam(value = "changeType") Integer changeType ){
//
//        try {
//            adminService.changeRole(userId, changeType);
//            return new BaseResponse<>(SUCCESS);
//        } catch (BaseException exception) {
//            return new BaseResponse<>(exception.getStatus());
//        }
//    }
}
