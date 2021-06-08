package com.app.edit.controller;

import com.app.edit.config.BaseException;
import com.app.edit.config.BaseResponse;
import com.app.edit.enums.AuthenticationCheck;
import com.app.edit.enums.UserRole;
import com.app.edit.provider.UserProvider;
import com.app.edit.request.user.*;
import com.app.edit.response.user.*;
import com.app.edit.service.UserService;
import com.app.edit.utils.JwtService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static com.app.edit.config.BaseResponseStatus.*;
import static com.app.edit.utils.ValidationRegex.*;

@Slf4j
@RequestMapping("/api")
@RestController
public class UserController {

    private final UserProvider userProvider;
    private final UserService userService;
    private final JwtService jwtService;

    @Autowired
    public UserController(UserProvider userProvider,
                          UserService userService,
                          JwtService jwtService) {
        this.userProvider = userProvider;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    /**
     * 회원 전체 조회 API
     * [GET] /users
     * @return BaseResponse<List<GetUsersRes>>
     */
    @GetMapping("/admin/users")
    @ApiOperation(value = "회원 전체 조회", notes = "회원 전체 조회")
    public BaseResponse<List<GetUserRes>> getUsers() {
        try {
            List<GetUserRes> getUsersResList = userProvider.retrieveUserList();
            return new BaseResponse<>(SUCCESS, getUsersResList);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 회원 추가
     * [POST] /users
     * @return BaseResponse<List<GetUsersRes>>
     */
    @PostMapping(value = "/users")
    @ApiOperation(value = "회원 추가", notes = "회원 추가")
    public BaseResponse<PostUserRes> createUsers(
            @RequestBody PostUserReq parameters) {

        //이메일 정규식
        if (parameters.getEmail() == null || parameters.getEmail().length() == 0) {
            return new BaseResponse<>(EMPTY_EMAIL);
        }
        if (!isRegexEmail(parameters.getEmail())){
            return new BaseResponse<>(INVALID_EMAIL);
        }

        //닉네임 정규식
        if (parameters.getNickname() == null || parameters.getNickname().length() == 0) {
            return new BaseResponse<>(EMPTY_NICKNAME);
        }
        if (!isRegexNickName(parameters.getNickname())){
            return new BaseResponse<>(INVALID_NICKNAME);
        }

        //이름 정규식
        if (parameters.getName() == null || parameters.getName().length() == 0) {
            return new BaseResponse<>(EMPTY_NAME);
        }
        if (!isRegexName(parameters.getName())){
            return new BaseResponse<>(INVALID_NAME);
        }


        //패스워드 정규식
        if (parameters.getPassword() == null || parameters.getPassword().length() == 0) {
            return new BaseResponse<>(EMPTY_PASSWORD);
        }
        if (!isRegexPassword(parameters.getPassword())){
            return new BaseResponse<>(INVALID_PASSWORD);
        }

        if (parameters.getAuthenticationPassword() == null || parameters.getAuthenticationPassword().length() == 0) {
            return new BaseResponse<>(EMPTY_CONFIRM_PASSWORD);
        }

        if (!isRegexPassword(parameters.getAuthenticationPassword())){
            return new BaseResponse<>(INVALID_CONFIRM_PASSWORD);
        }

        if (!parameters.getPassword().equals(parameters.getAuthenticationPassword())) {
            return new BaseResponse<>(DO_NOT_MATCH_PASSWORD);
        }

        //핸드폰 정규식
        if(parameters.getPhoneNumber() == null || parameters.getPhoneNumber().length() == 0) {
            return new BaseResponse<>(EMPTY_PHONENUMBER);
        }
        if(!isRegexPhoneNumber(parameters.getPhoneNumber())) {
            return new BaseResponse<>(INVALID_PHONENUMBER);
        }

        try {
            PostUserRes postUserRes = userService.createUserInfo(parameters);
            return new BaseResponse<>(SUCCESS, postUserRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 중복 이메일 및 닉네임 검증
     * [POST] /users/duplication
     */
    @PostMapping(value = "/users/duplication")
    @ApiOperation(value = "중복 이메일 및 닉네임 검증", notes = "중복 이메일 및 닉네임 검증" +
            "\n YES - 중복된 닉네임, 이메일 존재, NO - 중복된 닉네임,이메일 없음")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "email", value = "이메일 검사일 경우 nickName 비워서 전송"),
            @ApiImplicitParam(name = "nickName", value = "닉네임 검사일 경우 email 비워서 전송")
    })
    public BaseResponse<DuplicationCheck> duplicationEmail(
            @RequestBody PostDuplicationReq parameters) throws BaseException{

        String email = parameters.getEmail();
        String nickName = parameters.getNickName();

        if(nickName == null && email == null)
            throw new BaseException(EMPTY_CONTENT);

        if(nickName != null && email != null)
            throw new BaseException(INVAILD_CONTENT);

        if(nickName != null && !isRegexNickName(parameters.getNickName()))
            throw new BaseException(INVALID_NICKNAME);

        if(email != null && !isRegexEmail(parameters.getEmail()))
            throw new BaseException(INVALID_EMAIL);

        try {
            DuplicationCheck duplicationCheck = userProvider.checkDuplication(email,nickName);
            return new BaseResponse<>(SUCCESS, duplicationCheck);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 이메일 인증
     * [GET] /api/users/authentication-email
     */
    @PostMapping(value = "/users/authentication-email")
    @ApiOperation(value = "이메일 인증", notes = "이메일 인증")
    public BaseResponse<Void> AuthenticationEmail(
            @RequestBody PostEmailReq parameters) throws BaseException {

        if (parameters.getEmail() == null || parameters.getEmail().length() == 0) {
            return new BaseResponse<>(EMPTY_EMAIL);
        }
        if (!isRegexEmail(parameters.getEmail())){
            return new BaseResponse<>(INVALID_EMAIL);
        }

        try {
            userProvider.authenticationEmail(parameters.getEmail());
            return new BaseResponse<>(SUCCESS);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 이메일 인증 코드 검증
     * [GET] /api/users/authentication-code
     */
    @GetMapping(value = "/users/authentication-code")
    @ApiOperation(value = "이메일 인증 코드 검증", notes = "이메일 인증 코드 검증")
    public BaseResponse<AuthenticationCheck> AuthenticationCode(
            @RequestParam(value = "authenticationCode") String authenticationCode) {

        try {
            AuthenticationCheck authenticationCheck = userProvider.authenticationCode(authenticationCode);
            return new BaseResponse<>(SUCCESS, authenticationCheck);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 이메일 찾기
     * [POST] /api/users/email
     */
    @PostMapping(value = "/users/email")
    @ApiOperation(value = "이메일 찾기", notes = "이메일 찾기")
    public BaseResponse<GetEmailRes> searchEmail(
            @RequestBody PostSearchEmailReq parameters) throws BaseException {

        String name = parameters.getName();
        String phoneNumber = parameters.getPhoneNumber();

        if(parameters.getPhoneNumber() == null || parameters.getPhoneNumber().length() == 0) {
            return new BaseResponse<>(EMPTY_PHONENUMBER);
        }
        if(!isRegexPhoneNumber(parameters.getPhoneNumber())) {
            return new BaseResponse<>(INVALID_PHONENUMBER);
        }

        if (parameters.getName() == null || parameters.getName().length() == 0) {
            return new BaseResponse<>(EMPTY_NAME);
        }
        if (!isRegexName(parameters.getName())){
            return new BaseResponse<>(INVALID_NAME);
        }

        try {
            GetEmailRes getEmailRes = userProvider.searchEmail(name,phoneNumber);
            return new BaseResponse<>(SUCCESS , getEmailRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 비밀번호 찾기
     * [POST] /api/users/temporary-password
     */
    @PostMapping(value = "/users/temporary-password")
    @ApiOperation(value = "비밀번호 찾기", notes = " 찾기")
    public BaseResponse<Void> searchPassword(
            @RequestBody PatchSearchPasswordReq parameters) throws BaseException{

        String email = parameters.getEmail();
        String name = parameters.getName();
        String phoneNumber = parameters.getPhoneNumber();

        //이메일 정규식
        if (parameters.getEmail() == null || parameters.getEmail().length() == 0) {
            return new BaseResponse<>(EMPTY_EMAIL);
        }
        if (!isRegexEmail(parameters.getEmail())){
            return new BaseResponse<>(INVALID_EMAIL);
        }

        if (parameters.getName() == null || parameters.getName().length() == 0) {
            return new BaseResponse<>(EMPTY_NAME);
        }
        if (!isRegexName(parameters.getName())){
            return new BaseResponse<>(INVALID_NAME);
        }


        if(parameters.getPhoneNumber() == null || parameters.getPhoneNumber().length() == 0) {
            return new BaseResponse<>(EMPTY_PHONENUMBER);
        }
        if(!isRegexPhoneNumber(parameters.getPhoneNumber())) {
            return new BaseResponse<>(INVALID_PHONENUMBER);
        }


        try {
            userService.searchPassword(name, email, phoneNumber);
            return new BaseResponse<>(SUCCESS);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 비밀번호 인증
     * [GET] /api/users/authentication-password
     */
    @PostMapping(value = "/users/authentication-password")
    @ApiOperation(value = "비밀번호 인증", notes = "비밀번호 인증\n"+"YES = 인증됨, NO = 인증안됨")
    public BaseResponse<AuthenticationCheck> authenticationPassword (
            @RequestHeader(value = "X-ACCESS-TOKEN") String jwt,
            @RequestBody GetPasswordReq parameters) throws BaseException{

        if (parameters.getPassword() == null || parameters.getPassword().length() == 0) {
            return new BaseResponse<>(EMPTY_PASSWORD);
        }
        if (!isRegexPassword(parameters.getPassword())){
            return new BaseResponse<>(INVALID_PASSWORD);
        }

        try {
            Long userId = jwtService.getUserInfo().getUserId();

            if (userId == null || userId <= 0) {
                return new BaseResponse<>(EMPTY_USERID);
            }

            AuthenticationCheck authenticationCheck = userProvider.authenticationPassword(userId, parameters.getPassword());
            return new BaseResponse<>(SUCCESS, authenticationCheck);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 비밀번호 수정
     * [PATCH] /api/users/password
     */
    @PatchMapping(value = "/users/password")
    @ApiOperation(value = "비밀번호 수정", notes = "비밀번호 수정")
    public BaseResponse<GetNickNameRes> UpdatePassword(
            //@RequestHeader(value = "X-ACCESS-TOKEN") String jwt,
            @RequestBody UpdatePasswordReq parameters) {

        try {
            Long userId = jwtService.getUserInfo().getUserId();
            String password = parameters.getPassword();
            String authenticationPassword = parameters.getAuthenticationPassword();

            if (parameters.getPassword() == null || parameters.getPassword().length() == 0) {
                return new BaseResponse<>(EMPTY_PASSWORD);
            }
            if (!isRegexPassword(parameters.getPassword())){
                return new BaseResponse<>(INVALID_PASSWORD);
            }

            if (parameters.getAuthenticationPassword() == null || parameters.getAuthenticationPassword().length() == 0) {
                return new BaseResponse<>(EMPTY_CONFIRM_PASSWORD);
            }

            if (!isRegexPassword(parameters.getAuthenticationPassword())){
                return new BaseResponse<>(INVALID_CONFIRM_PASSWORD);
            }

            if (!password.equals(authenticationPassword)) {
                return new BaseResponse<>(DO_NOT_MATCH_PASSWORD);
            }

            if (userId == null || userId <= 0) {
                return new BaseResponse<>(EMPTY_USERID);
            }
            GetNickNameRes getNickNameRes = userService.updatePassword(userId,password);
            return new BaseResponse<>(SUCCESS, getNickNameRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * 내 프로필 조회
     * [GET] /api/users/profile
     */
    @GetMapping(value = "/users/profile")
    @ApiOperation(value = "내 프로필 조회", notes = "내 프로필 조회")
    public BaseResponse<GetProfileRes> getProfile(
            @RequestHeader(value = "X-ACCESS-TOKEN") String jwt){

        try {
            Long userId = jwtService.getUserInfo().getUserId();

            if (userId == null || userId <= 0) {
                return new BaseResponse<>(EMPTY_USERID);
            }
            GetProfileRes getProfileRes = userProvider.retrieveProfile(userId);
            return new BaseResponse<>(SUCCESS, getProfileRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 내 이름 조회
     * [GET] /api/users/nick
     */
    @GetMapping(value = "/users/nickname")
    @ApiOperation(value = "닉네임 조회", notes = "닉네임 조회")
    public BaseResponse<GetNickNameRes> getNickName(
            //@RequestHeader(value = "X-ACCESS-TOKEN") String jwt
            ){

        try {
            Long userId = jwtService.getUserInfo().getUserId();

            if (userId == null || userId <= 0) {
                return new BaseResponse<>(EMPTY_USERID);
            }
            GetNickNameRes getNickNameRes = userProvider.retrieveNickName(userId);
            return new BaseResponse<>(SUCCESS, getNickNameRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }



    /**
     * 캐릭터 및 색상 변경
     * [PATCH] /api/users/profile
     */
    @PatchMapping(value = "/users/profile")
    @ApiOperation(value = "캐릭터 및 색상 변경", notes = "캐릭터 및 색상 변경")
    public BaseResponse<Void> UpdateProfile(
            @RequestHeader(value = "X-ACCESS-TOKEN") String jwt,
            @RequestBody UpdateProfileReq parameters
            //@RequestParam(value = "colorName") String colorName,
            //@RequestParam(value = "emotionName") String emotionName
            ) throws BaseException{

        String colorName = parameters.getColorName();
        String emotionName = parameters.getEmotionName();

        if (colorName == null || colorName.length() == 0) {
            return new BaseResponse<>(EMPTY_COLORNAME);
        }

        if (emotionName == null || emotionName.length() ==  0) {
            return new BaseResponse<>(EMPTY_EMOTIONNAME);
        }
        try {
            Long userId = jwtService.getUserInfo().getUserId();

            if (userId == null || userId <= 0) {
                return new BaseResponse<>(EMPTY_USERID);
            }
            userService.updateProfile(userId,colorName,emotionName);
            return new BaseResponse<>(SUCCESS);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    //TODO 이미 처리되었는지 확인하기
    /**
     * 멘토 인증
     * [POST] /users/authentication
     * @return BaseResponse<Void>
     */
    @PostMapping(value = "/users/authentication")
    @ApiOperation(value = "멘토 인증", notes = "멘토 인증")
    public BaseResponse<Void> userAuthentication(
            @RequestHeader("X-ACCESS-TOKEN") String jwt,
            @RequestBody PostMentorAuthenticationReq request) throws IOException {
        try {

            GetUserInfo getUserInfo = jwtService.getUserInfo();
            Long userId = getUserInfo.getUserId();
            UserRole userRole = Arrays.stream(UserRole.values())
                    .filter(userRole1 -> userRole1.name().equals(getUserInfo.getRole()))
                    .findFirst()
                    .orElseThrow(() -> new BaseException(FAILED_TO_GET_ROLE));

            if (userId == null || userId <= 0) {
                return new BaseResponse<>(EMPTY_USERID);
            }

            if( userRole.equals(UserRole.MENTEE))
                return new BaseResponse<>(UNAUTHORIZED_AUTHORITY);

            userService.AuthenticationMentor(userId, request);

            return new BaseResponse<>(SUCCESS);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 회원 탈퇴
     * [DELETE] /api/users
     */
    @DeleteMapping(value = "/users")
    @ApiOperation(value = "회원 탈퇴", notes = "회원 탈퇴")
    public BaseResponse<GetNickNameRes> UpdateProfile(
            //@RequestHeader(value = "X-ACCESS-TOKEN") String jwt,
            @Valid
            @RequestBody DeleteUserReq parameters){

        try {
            Long userId = jwtService.getUserInfo().getUserId();

            if (userId == null || userId <= 0) {
                return new BaseResponse<>(EMPTY_USERID);
            }
            GetNickNameRes getNickNameRes = userService.deleteUser(userId,parameters);
            return new BaseResponse<>(SUCCESS , getNickNameRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 멘토 인증 상태 조회
     * [GET] /users/authentication
     * @return BaseResponse<Void>
     */
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-ACCESS-TOKEN", value = "jwt"),
    })
    @GetMapping(value = "/users/authentication")
    @ApiOperation(value = "멘토 인증 상태 조회", notes = "멘토 인증")
    public BaseResponse<GetAuthenticationRes> userAuthentication(
            //@RequestHeader("X-ACCESS-TOKEN") String jwt
    ){

        try {

            GetUserInfo getUserInfo = jwtService.getUserInfo();
            Long userId = getUserInfo.getUserId();
            UserRole userRole = Arrays.stream(UserRole.values())
                    .filter(userRole1 -> userRole1.name().equals(getUserInfo.getRole()))
                    .findFirst()
                    .orElseThrow(() -> new BaseException(FAILED_TO_GET_ROLE));

            if (userId == null || userId <= 0) {
                return new BaseResponse<>(EMPTY_USERID);
            }

            if (userRole.equals(UserRole.MENTEE))
                throw new BaseException(UNAUTHORIZED_AUTHORITY);

            GetAuthenticationRes getAuthenticationRes = userProvider.AuthenticationMentor(userId);

            return new BaseResponse<>(SUCCESS, getAuthenticationRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 직군 변경
     * [PATCH] /api/users
     */
    @PatchMapping(value = "/users/jobs")
    @ApiOperation(value = "직군 변경", notes = "직군 변경")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "jobName", value = "개발\n 경영\n  기획\n  디자인 \n 마케팅 / 홍보 \n 서무 / 서비스 \n 생산 / 기술\n  영업\n  인사 / 교육 \n 재무 / 회계 \n 총무 \n C/S \n 기타"),
            @ApiImplicitParam(name = "etcJobName", value = "기타일 경우 내용 추가\n 기타가 아니면 NONE")
    })
    public BaseResponse<Void> UpdateJob(
            @RequestHeader(value = "X-ACCESS-TOKEN") String jwt,
            @RequestBody UpdateJobReq parameters)
            //@RequestParam(value = "jobName") String jobName,
            //@RequestParam(value = "etcJobName") String etcJobName)
    {

        String jobName = parameters.getJobName();
        String etcJobName = parameters.getEtcJobName();

        if (jobName == null || jobName.length() == 0) {
            return new BaseResponse<>(EMPTY_JOBNAME);
        }
        if (etcJobName == null || etcJobName.length() == 0) {
            return new BaseResponse<>(EMPTY_ETCJOBNAME);
        }

        try {
            Long userId = jwtService.getUserInfo().getUserId();

            if (userId == null || userId <= 0) {
                return new BaseResponse<>(EMPTY_USERID);
            }
            userService.updateJobs(userId,jobName,etcJobName);
            return new BaseResponse<>(SUCCESS);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 회원 역할 조회 API
     * [GET] /users/authentication
     * @return BaseResponse<Void>
     */
    @GetMapping(value = "/roles")
    @ApiOperation(value = "회원 역할 조회", notes = "회원 역할 조회 API")
    public BaseResponse<GetRoleRes> userRole(
            @RequestHeader("X-ACCESS-TOKEN") String jwt) throws BaseException{

        try {

            GetUserInfo getUserInfo = jwtService.getUserInfo();

            Long userId = getUserInfo.getUserId();

            if (userId == null || userId <= 0) {
                return new BaseResponse<>(EMPTY_USERID);
            }

            GetRoleRes getRoleRes = userProvider.retrieveRole(userId);


            return new BaseResponse<>(SUCCESS, getRoleRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 멘토 -> 멘티 역할 변경
     * [PATCH] /change-roles/to-mentee
     * @return BaseResponse<PATCH>
     */
    @PatchMapping(value = "/change-roles/to-mentee")
    @ApiOperation(value = "멘토 -> 멘티 역할 변경", notes = "멘토 -> 멘티 역할 변경 API")
    public BaseResponse<Long> changeRoleToMentee(
            //@RequestHeader("X-ACCESS-TOKEN") String jwt,
            @RequestBody PatchRoleReq patchRoleReq) throws BaseException{

        try {

            GetUserInfo getUserInfo = jwtService.getUserInfo();
            Long userId = getUserInfo.getUserId();

            if (userId == null || userId <= 0) {
                return new BaseResponse<>(EMPTY_USERID);
            }

            return new BaseResponse<>(SUCCESS, userService.ChangeRoleToMentee(userId, patchRoleReq));
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 멘티 -> 멘토 역할 변경 신청 API
     * @param request
     * @return
     * @throws BaseException
     */
    @ApiOperation(value = "멘티 -> 멘토 역할 변경")
    @PatchMapping("/change-roles/to-mentor")
    public BaseResponse<Long> changeRoleToMentor(@RequestBody @Valid PatchRoleReq request) throws BaseException {
        return new BaseResponse(SUCCESS, userService.changeRoleToMentor(request));
    }



    /**
     * 나의 에딧 화면 조회
     * [GET] /api/users/mypage
     */
    @GetMapping(value = "/users/mypage")
    @ApiOperation(value = "나의 에딧 화면 조회", notes = "나의 에딧 화면 조회")
    public BaseResponse<GetProfileRes> getMyPage(
            //@RequestHeader(value = "X-ACCESS-TOKEN") String jwt
    ){

        try {
            Long userId = jwtService.getUserInfo().getUserId();

            if (userId == null || userId <= 0) {
                return new BaseResponse<>(EMPTY_USERID);
            }
            GetProfileRes getProfileRes = userProvider.retrieveProfile(userId);
            return new BaseResponse<>(SUCCESS, getProfileRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 나의 코인 관리
     * [GET] /api/coins
     */
    @GetMapping(value = "/users/coins")
    @ApiOperation(value = "나의 코인 조회", notes = "나의 코인 조회")
    public BaseResponse<GetCoinRes> getMyCoin(
            @RequestHeader(value = "X-ACCESS-TOKEN") String jwt){

        try {
            Long userId = jwtService.getUserInfo().getUserId();

            if (userId == null || userId <= 0) {
                return new BaseResponse<>(EMPTY_USERID);
            }
            GetCoinRes getCoinRes = userProvider.retrieveCoin(userId);
            return new BaseResponse<>(SUCCESS, getCoinRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

}
