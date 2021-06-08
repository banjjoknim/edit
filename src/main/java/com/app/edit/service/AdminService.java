package com.app.edit.service;

import com.app.edit.config.BaseException;
import com.app.edit.domain.certificationRequest.CertificationRequest;
import com.app.edit.domain.certificationRequest.CertificationRequestRepository;
import com.app.edit.domain.user.UserInfo;
import com.app.edit.domain.user.UserInfoRepository;
import com.app.edit.enums.AuthenticationCheck;
import com.app.edit.enums.IsProcessing;
import com.app.edit.enums.State;
import com.app.edit.enums.UserRole;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.app.edit.config.BaseResponseStatus.*;

@Service
public class AdminService {

    private final UserInfoRepository userInfoRepository;
    private final CertificationRequestRepository certificationRequestRepository;

    public AdminService(UserInfoRepository userInfoRepository,
                        CertificationRequestRepository certificationRequestRepository) {
        this.userInfoRepository = userInfoRepository;
        this.certificationRequestRepository = certificationRequestRepository;
    }

    /**
     * 멘티 -> 멘토 인증 관리
     * @param userId
     * @param authenticationType
     */
    @Transactional
    public void manageAuthentication(Long userId, Integer authenticationType) throws BaseException{

        UserInfo userInfo = userInfoRepository.findByStateAndId(State.ACTIVE,userId)
                .orElseThrow(() -> new BaseException(FAILED_TO_GET_USER));

        //인증 승낙
        if(authenticationType == 1){
            CertificationRequest certificationRequest =
                    certificationRequestRepository.findTop1ByIsProcessingAndUserInfoOrderByCreatedAtDesc(IsProcessing.NO,userInfo)
                            .orElseThrow(() -> new BaseException(FAILED_TO_GET_CERTIFICATION_REQUEST));

            certificationRequest.setIsProcessing(IsProcessing.YES);
            userInfo.setIsCertificatedMentor(AuthenticationCheck.YES);
        }

        //인증 거절
        if(authenticationType == 2){
            //인증 요청 조회
            CertificationRequest certificationRequest =
                    certificationRequestRepository.findTop1ByIsProcessingAndUserInfoOrderByCreatedAtDesc(IsProcessing.NO,userInfo)
                            .orElseThrow(() -> new BaseException(FAILED_TO_GET_CERTIFICATION_REQUEST));

            //인증 처리 후 멘티 -> 멘토로 역할 변경
            certificationRequest.setIsProcessing(IsProcessing.YES);
        }

    }

    /**
     * 관리자 역할 변경 API
     * 하나라도 실패할 경우 rollback -> transactional
     * @param userId
     * @param changeType
     */
    @Transactional
    public void changeRole(Long userId, Integer changeType) throws BaseException{


        UserInfo userInfo = userInfoRepository.findByStateAndId(State.ACTIVE,userId)
                .orElseThrow(() -> new BaseException(FAILED_TO_GET_USER));

        //멘티 -> 멘토
        if(changeType == 1){

            //인증 요청 조회
            CertificationRequest certificationRequest =
                    certificationRequestRepository.findTop1ByIsProcessingAndUserInfoOrderByCreatedAtDesc(IsProcessing.NO,userInfo)
                    .orElseThrow(() -> new BaseException(FAILED_TO_GET_CERTIFICATION_REQUEST));

            //인증 처리 후 멘티 -> 멘토로 역할 변경
            certificationRequest.setIsProcessing(IsProcessing.YES);
            userInfo.setUserRole(UserRole.MENTOR);
        }

        //역할 변경 거절
        if(changeType == 2){
            //인증 요청 조회
            CertificationRequest certificationRequest =
                    certificationRequestRepository.findTop1ByIsProcessingAndUserInfoOrderByCreatedAtDesc(IsProcessing.NO,userInfo)
                            .orElseThrow(() -> new BaseException(FAILED_TO_GET_CERTIFICATION_REQUEST));

            //인증 처리 후 멘티 -> 멘토로 역할 변경
            certificationRequest.setIsProcessing(IsProcessing.YES);
        }
    }
}
