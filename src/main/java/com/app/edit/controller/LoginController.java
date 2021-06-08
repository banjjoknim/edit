package com.app.edit.controller;

import com.app.edit.config.BaseException;
import com.app.edit.config.BaseResponse;
import com.app.edit.provider.UserInfoProvider;
import com.app.edit.provider.UserProvider;
import com.app.edit.request.user.PostLoginReq;
import com.app.edit.response.user.GetJoinedUserInfoRes;
import com.app.edit.response.user.PostUserRes;
import com.app.edit.service.UserService;
import com.app.edit.utils.JwtService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.app.edit.config.BaseResponseStatus.*;
import static com.app.edit.utils.ValidationRegex.isRegexEmail;
import static com.app.edit.utils.ValidationRegex.isRegexPassword;

@Slf4j
@RequestMapping("/api")
@RestController
public class LoginController {

    private final UserProvider userProvider;
    private final UserInfoProvider userInfoProvider;
    private final UserService userService;
    private final JwtService jwtService;

    @Autowired
    public LoginController(UserProvider userProvider, UserInfoProvider userInfoProvider,
                           UserService userService, JwtService jwtService) {
        this.userProvider = userProvider;
        this.userInfoProvider = userInfoProvider;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    /**
     * 로그인
     * [GET] /api/login
     */
    @PostMapping(value = "/login")
    @ApiOperation(value = "로그인", notes = "로그인")
    public BaseResponse<PostUserRes> login(
            @RequestBody PostLoginReq parameters) throws BaseException{

        if(parameters.getEmail() == null || parameters.getEmail().length() == 0)
            throw new BaseException(EMPTY_EMAIL);

        if (!isRegexEmail(parameters.getEmail())){
            throw new BaseException(INVALID_EMAIL);
        }

        if(parameters.getPassword() == null || parameters.getPassword().length() == 0)
            throw new BaseException(EMPTY_PASSWORD);


        if (!isRegexPassword(parameters.getPassword())){
            throw new BaseException(INVALID_PASSWORD);
        }

        try {
            PostUserRes postUserRes = userProvider.login(parameters);
            return new BaseResponse<>(SUCCESS, postUserRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    //TODO 로그아웃 구현하기
    /**
     * 로그아웃
     * [Post] /api/login
     */
    @PostMapping(value = "/logout")
    @ApiOperation(value = "로그아웃", notes = "로그아웃")
    public BaseResponse<Void> logout(){

        try {
            userProvider.logout();
            return new BaseResponse<>(SUCCESS);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 스플래시 화면 자동 로그인시 유저 검증 API
     * @return
     * @throws BaseException
     */
    @ApiOperation(value = "스플래시 화면 자동 로그인시 유저 검증 API")
    @GetMapping("/auto-login")
    public BaseResponse<GetJoinedUserInfoRes> validateIsJoinedUser() throws BaseException {
        return new BaseResponse(SUCCESS, userInfoProvider.getJoinedUserInfo());
    }
}
