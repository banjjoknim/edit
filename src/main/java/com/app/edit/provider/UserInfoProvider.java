package com.app.edit.provider;

import com.app.edit.config.BaseException;
import com.app.edit.domain.changerolereqeust.ChangeRoleRequest;
import com.app.edit.domain.user.UserInfo;
import com.app.edit.domain.user.UserInfoRepository;
import com.app.edit.enums.AuthenticationCheck;
import com.app.edit.enums.State;
import com.app.edit.response.user.GetJoinedUserInfoRes;
import com.app.edit.response.user.GetUserInfo;
import com.app.edit.service.ChangeRoleRequestService;
import com.app.edit.utils.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.app.edit.config.BaseResponseStatus.*;

@Slf4j
@Transactional
@Service
public class UserInfoProvider {

    private final UserInfoRepository userInfoRepository;
    private final ChangeRoleRequestProvider changeRoleRequestProvider;
    private final ChangeRoleRequestService changeRoleRequestService;
    private final JwtService jwtService;

    @Autowired
    public UserInfoProvider(UserInfoRepository userInfoRepository, ChangeRoleRequestProvider changeRoleRequestProvider,
                            ChangeRoleRequestService changeRoleRequestService, JwtService jwtService) {
        this.userInfoRepository = userInfoRepository;
        this.changeRoleRequestProvider = changeRoleRequestProvider;
        this.changeRoleRequestService = changeRoleRequestService;
        this.jwtService = jwtService;
    }

    public UserInfo getUserInfoById(Long userInfoId) throws BaseException {
        Optional<UserInfo> userInfo = userInfoRepository.findById(userInfoId);
        if (userInfo.isEmpty()) {
            throw new BaseException(NOT_FOUND_USER_INFO);
        }
        if (userInfo.get().getState().equals(State.INACTIVE)) {
            throw new BaseException(ALREADY_DELETED_USER);
        }
        return userInfo.get();
    }

    public GetJoinedUserInfoRes getJoinedUserInfo() throws BaseException {
        GetUserInfo userInfoInToken = jwtService.getUserInfo();
        Long userInfoId = userInfoInToken.getUserId();
        String userRole = userInfoInToken.getRole();
        UserInfo userInfo = getUserInfoById(userInfoId);
        if (!userInfo.getUserRole().name().equals(userRole)) {
            throw new BaseException(TOKEN_INFORMATION_IS_NOT_EQUALS_IN_SERVER);
        }
        boolean isCertificatedMentor = userInfo.getIsCertificatedMentor().equals(AuthenticationCheck.YES);
        Optional<ChangeRoleRequest> changeRoleRequest = changeRoleRequestProvider.getChangeRoleRequestByUserInfo(userInfo);
        changeRoleRequest.ifPresent(request -> request.process());
        log.info("{} 님이 자동 로그인 하셨습니다.", userInfo.getNickName());
        return new GetJoinedUserInfoRes(userInfo.getUserRole(), isCertificatedMentor);
    }
}
