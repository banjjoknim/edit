package com.app.edit.service;

import com.app.edit.config.BaseException;
import com.app.edit.config.secret.Secret;
import com.app.edit.domain.certificationRequest.CertificationRequest;
import com.app.edit.domain.certificationRequest.CertificationRequestRepository;
import com.app.edit.domain.changerolecategory.ChangeRoleCategory;
import com.app.edit.domain.changerolecategory.ChangeRoleCategoryRepository;
import com.app.edit.domain.changerolereqeust.ChangeRoleRequest;
import com.app.edit.domain.changerolereqeust.ChangeRoleRequestRepository;
import com.app.edit.domain.job.Job;
import com.app.edit.domain.job.JobRepository;
import com.app.edit.domain.profilecolor.ProfileColor;
import com.app.edit.domain.profilecolor.ProfileColorRepository;
import com.app.edit.domain.profileemotion.ProfileEmotion;
import com.app.edit.domain.profileemotion.ProfileEmotionRepository;
import com.app.edit.domain.user.UserInfo;
import com.app.edit.domain.user.UserInfoRepository;
import com.app.edit.domain.userprofile.UserProfile;
import com.app.edit.domain.userprofile.UserProfileRepository;
import com.app.edit.enums.AuthenticationCheck;
import com.app.edit.enums.IsProcessing;
import com.app.edit.enums.State;
import com.app.edit.enums.UserRole;
import com.app.edit.provider.ChangeRoleRequestProvider;
import com.app.edit.provider.UserInfoProvider;
import com.app.edit.provider.UserProvider;
import com.app.edit.request.user.DeleteUserReq;
import com.app.edit.request.user.PatchRoleReq;
import com.app.edit.request.user.PostMentorAuthenticationReq;
import com.app.edit.request.user.PostUserReq;
import com.app.edit.response.user.GetNickNameRes;
import com.app.edit.response.user.PostUserRes;
import com.app.edit.utils.AES128;
import com.app.edit.utils.JwtService;
import com.app.edit.utils.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Optional;

import static com.app.edit.config.BaseResponseStatus.*;
import static com.app.edit.config.Constant.*;

@RequiredArgsConstructor
@Transactional
@Service
public class UserService {

    private final UserInfoRepository userInfoRepository;
    private final EmailSenderService emailSenderService;
    private final JobRepository jobRepository;
    private final UserProvider userProvider;
    private final UserInfoProvider userInfoProvider;
    private final UserProfileRepository userProfileRepository;
    private final ProfileEmotionRepository profileEmotionRepository;
    private final ProfileColorRepository profileColorRepository;
    private final JwtService jwtService;
    private final S3Service s3Service;
    private final CertificationRequestRepository certificationRequestRepository;
    private final ChangeRoleCategoryRepository changeRoleCategoryRepository;
    private final ChangeRoleRequestRepository changeRoleRequestRepository;
    private final ChangeRoleRequestProvider changeRoleRequestProvider;


    @Transactional
    public PostUserRes createUserInfo(PostUserReq parameters) throws BaseException {

        UserInfo existsUser = null;
        try {
            // 1-1. 이미 존재하는 회원이 있는지 조회
            existsUser = userProvider.retrieveUserByEmail(parameters.getEmail());
        } catch (BaseException exception) {
            // 1-2. 이미 존재하는 회원이 없다면 그대로 진행
            if (exception.getStatus() != NOT_FOUND_USER) {
                throw exception;
            }
        }
        // 1-3. 이미 존재하는 회원이 있다면 return DUPLICATED_USER
        if (existsUser != null) {
            throw new BaseException(DUPLICATED_USER);
        }

        // 2. 유저 정보 생성
        String EncodingPassword = "";
        try {
            EncodingPassword = new AES128(Secret.USER_INFO_PASSWORD_KEY).encrypt(parameters.getPassword());
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_POST_USER);
        }
        UserInfo newUser = UserInfo.builder()
                .name(parameters.getName())
                .nickName(parameters.getNickname())
                .password(EncodingPassword)
                .email(parameters.getEmail())
                .userRole(parameters.getUserRole())
                .coinCount(0L)
                .job(jobRepository.findByName(parameters.getJobName())
                        .orElseThrow(() -> new BaseException(FAILED_TO_GET_JOB)))
                .etcJobName(parameters.getEtcJobName().equals("NONE") ? "NONE": parameters.getEtcJobName())
                .phoneNumber(parameters.getPhoneNumber())
                .build();

        // 3. 유저 정보 저장
        try {
            newUser = userInfoRepository.save(newUser);
        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_POST_USER);
        }

        UserProfile userProfile = UserProfile.builder()
                .profileColor(profileColorRepository.findById(1L)
                        .orElseThrow(() -> new BaseException(NOT_FOUND_COLOR)))
                .profileEmotion(profileEmotionRepository.findById(1L)
                        .orElseThrow(() -> new BaseException(NOT_FOUND_EMOTION)))
                .userInfo(newUser)
                .build();

        try{
            UserProfile profile = userProfileRepository.save(userProfile);
            newUser.setUserProfile(profile);
        }catch (Exception exception){
            throw new BaseException(FAILED_TO_POST_USER_PROFILE);
        }

        // 4. JWT 생성
        String jwt = jwtService.createJwt(newUser.getId(),newUser.getUserRole());


        return PostUserRes.builder()
                .jwt(jwt)
                .userRole(newUser.getUserRole())
                .build();
    }

    /**
     * 비밀번호 찾기
     * @param name
     * @param email
     * @param phoneNumber
     */
    @Transactional
    public void searchPassword(String name, String email, String phoneNumber) throws BaseException {

        UserInfo user = userInfoRepository
                .findByStateAndNameAndPhoneNumberAndEmail(State.ACTIVE, name,phoneNumber,email)
                .orElseThrow(() -> new BaseException(FAILED_TO_GET_USER));

        //임시 비밀번호 발급
        String temporaryPassword = emailSenderService.createKey("password");

        ArrayList<String> to = new ArrayList<>();
        to.add(email);
        String subject = "<임시 비밀번호 발급>";
        StringBuilder emailContent = new StringBuilder();
        emailContent.append("<!DOCTYPE html>");
        emailContent.append("<html>");
        emailContent.append("<head>");
        emailContent.append("</head>");
        emailContent.append("<body>");
        emailContent.append(
                " <div" 																																																	+
                        "	style=\"font-family: 'Apple SD Gothic Neo', 'sans-serif' !important; width: 800px; height: 600px; border-top: 4px solid #5a32dc; margin: 100px auto; padding: 30px 0; box-sizing: border-box;\">"		+
                        "	<h1 style=\"margin: 0; padding: 0 5px; font-size: 28px; font-weight: 400;\">"																															+
                        "		<span style=\"font-size: 15px; margin: 0 0 10px 3px;\">EDIT.</span><br />"																													+
                        "		<span style=\"color: #5a32dc\">임시 비밀번호 발급</span> 안내입니다."																																				+
                        "	</h1>\n"																																																+
                        "	<p style=\"font-size: 16px; line-height: 26px; margin-top: 50px; padding: 0 5px;\">"																													+
                        "		안녕하세요.<br />"																																													+
                        "		EDIT. 임시 비밀번호 발급 관련된 메일 전송입니다.<br />"																																						+
                        "		임시 비밀번호는 "+ temporaryPassword + "입니다<br />"																													+
                        "		감사합니다."																																															+
                        "	</p>"																																																	+
                        "	<div style=\"border-top: 1px solid #DDD; padding: 5px;\"></div>"																																		+
                        " </div>"
        );
        emailContent.append("</body>");
        emailContent.append("</html>");

        // when
        String encodingPassword;
        try {
            encodingPassword = new AES128(Secret.USER_INFO_PASSWORD_KEY).encrypt(temporaryPassword);
        }catch (Exception ignored) {
            throw new BaseException(FAILED_TO_UPDATE_USER);
        }
        emailSenderService.send(subject, emailContent.toString(), to);
        user.setPassword(encodingPassword);
    }

    /**
     * 비밀번호 수정
     * @param userId
     * @param password
     * @throws BaseException
     */
    @Transactional
    public GetNickNameRes updatePassword(Long userId, String password) throws BaseException {

        UserInfo userInfo = userInfoRepository.findByStateAndId(State.ACTIVE,userId)
                .orElseThrow(() -> new BaseException(FAILED_TO_GET_USER));

        String encodingPassword;
        try{
            encodingPassword = new AES128(Secret.USER_INFO_PASSWORD_KEY).encrypt(password);
        }catch (Exception ignore){
            throw new BaseException(FAILED_TO_ENCRYPT_PASSWORD);
        }

        userInfo.setPassword(encodingPassword);
        return GetNickNameRes.builder()
                .nickName(userInfo.getNickName())
                .build();
    }

    /**
     * 내 프로필 수정
     * @param userId
     * @param colorName
     * @param emotionName
     * @throws BaseException
     */
    @Transactional
    public void updateProfile(Long userId, String colorName, String emotionName) throws BaseException {

        UserInfo userInfo = userInfoRepository.findByStateAndId(State.ACTIVE,userId)
                .orElseThrow(() -> new BaseException(FAILED_TO_GET_USER));

        ProfileColor profileColor = profileColorRepository.findByStateAndName(State.ACTIVE, colorName)
                .orElseThrow(() -> new BaseException(NOT_FOUND_COLOR));

        ProfileEmotion profileEmotion = profileEmotionRepository.findByStateAndName(State.ACTIVE, emotionName)
                .orElseThrow(() -> new BaseException(NOT_FOUND_EMOTION));

        userInfo.getUserProfile().setProfileEmotion(profileEmotion);
        userInfo.getUserProfile().setProfileColor(profileColor);

    }

    /**
     * 회원 탈퇴
     * @param userId
     * @param parameters
     * @throws BaseException
     */
    @Transactional
    public GetNickNameRes deleteUser(Long userId, DeleteUserReq parameters) throws BaseException {

        UserInfo userInfo = userInfoRepository.findByStateAndId(State.ACTIVE,userId)
                .orElseThrow(() -> new BaseException(FAILED_TO_GET_USER));

        if(parameters.getWithdrawalContent().equals("기타"))
            userInfo.setEtcWithdrawalContent(parameters.getEtcWithdrawalContent());

        userInfo.setWithdrawalContent(parameters.getWithdrawalContent());
        userInfo.setState(State.INACTIVE);

        return GetNickNameRes.builder()
                .nickName(userInfo.getNickName())
                .build();
    }

    /**
     * 멘토 인증 신청
     * @param userId
     * @param request
     * @throws IOException
     * @throws BaseException
     */
    public void AuthenticationMentor(Long userId, PostMentorAuthenticationReq request) throws IOException, BaseException {

        String authenticationFile = request.getAuthenticationImage();
        byte[] decodedFile = Base64.getMimeDecoder().decode(authenticationFile.substring(authenticationFile.indexOf(",") + 1));
        UserInfo userInfo = userInfoRepository.findByStateAndId(State.ACTIVE,userId)
                .orElseThrow(() -> new BaseException(NOT_FOUND_USER));
        String imgPath = s3Service.upload(decodedFile, userId);

        CertificationRequest certificationRequest = CertificationRequest.builder()
                .userInfo(userInfo)
                .imageUrl(imgPath)
                .build();

        try{
            certificationRequestRepository.save(certificationRequest).getImageUrl();
        }catch (Exception exception){
            throw new BaseException(FAILED_TO_POST_CERTIFICATION_REQUEST);
        }
    }

    /**
     * 직군 변경
     * @param userId
     * @param jobName
     * @param etcJobName
     */
    @Transactional
    public void updateJobs(Long userId, String jobName, String etcJobName) throws BaseException {

        UserInfo userInfo = userInfoRepository.findByStateAndId(State.ACTIVE,userId)
                .orElseThrow(() -> new BaseException(NOT_FOUND_USER));

        if(etcJobName.equals("NONE"))
            userInfo.setEtcJobName(etcJobName);

        Job job = jobRepository.findByName(jobName)
                .orElseThrow(() -> new BaseException(FAILED_TO_GET_JOB));

        userInfo.setJob(job);
    }

    /**
     * 멘토 -> 멘티 역할 변경
     * @param userId
     * @return
     */
    @Transactional
    public Long ChangeRoleToMentee(Long userId, PatchRoleReq patchRoleReq) throws BaseException{

        // 유저 조회
        UserInfo userInfo = userInfoRepository.findByStateAndId(State.ACTIVE, userId)
                .orElseThrow(() -> new BaseException(FAILED_TO_GET_USER));

        //이미 멘티일 경우
        if(userInfo.getUserRole().equals(UserRole.MENTEE))
            throw new BaseException(ALREADY_ROLE_MENTEE);

        String changeContent = patchRoleReq.getChangeContent();

        String etcChangeContent = patchRoleReq.getEtcChangeContent();

        if(etcChangeContent == null)
            etcChangeContent = "NONE";

        //카테고리 조회
        ChangeRoleCategory changeRoleCategory = changeRoleCategoryRepository.findByName(changeContent)
                .orElseThrow(() -> new BaseException(FAILED_TO_GET_CAHNGE_ROLE_CATEGORY));

        // 역할 변경 신청 객체 생성
        ChangeRoleRequest changeRoleRequest = ChangeRoleRequest.builder()
                .userInfo(userInfo)
                .changeRoleCategory(changeRoleCategory)
                .isProcessing(IsProcessing.YES)
                .previousRole(userInfo.getUserRole())
                .content(etcChangeContent)
                .build();

        //역할 변경 신청 저장
        try {
            changeRoleRequestRepository.save(changeRoleRequest);
        }catch (Exception exception){
            throw new BaseException(FAILED_TO_POST_CAHNGE_ROLE_REQUEST);
        }

        // 멘티로 변경
        userInfo.setUserRole(UserRole.MENTEE);
        userInfo.setIsCertificatedMentor(AuthenticationCheck.NO);

        userProvider.logout();

        return userInfo.getId();
    }

    @Transactional
    public Long changeRoleToMentor(PatchRoleReq request) throws BaseException {
        Long userInfoId = jwtService.getUserInfo().getUserId();
        UserInfo userInfo = userInfoProvider.getUserInfoById(userInfoId);
        validateChangeRoleRequestIsExist(userInfo);
        String changeContent = request.getChangeContent();
        String etcChangeContent = request.getEtcChangeContent();

        ChangeRoleCategory changeRoleCategory = changeRoleCategoryRepository.findByName(changeContent)
                .orElseThrow(() -> new BaseException(FAILED_TO_GET_CAHNGE_ROLE_CATEGORY));
        if (changeContent.equals(ETC_CHANGE_ROLE_CATEGORY_NAME)) {
            validateEtcChangeContent(etcChangeContent);
            changeContent = etcChangeContent;
        }
        ChangeRoleRequest changeRoleRequest = ChangeRoleRequest.builder()
                .content(changeContent)
                .isProcessing(IsProcessing.NO)
                .changeRoleCategory(changeRoleCategory)
                .previousRole(userInfo.getUserRole())
                .build();
        userInfo.addChangeRoleRequest(changeRoleRequest);
        changeRoleRequestRepository.save(changeRoleRequest);
        userInfo.changeUserRole();

        return userInfoId;
    }

    private void validateChangeRoleRequestIsExist(UserInfo userInfo) throws BaseException {
        Optional<ChangeRoleRequest> changeRoleRequest = changeRoleRequestProvider.getChangeRoleRequestByUserInfo(userInfo);
        if (changeRoleRequest.isPresent()) {
            throw new BaseException(ALREADY_EXIST_CHANGE_ROLE_REQUEST);
        }
    }

    private void validateEtcChangeContent(String etcChangeContent) throws BaseException {
        if (NONE.equals(etcChangeContent) || etcChangeContent.isBlank()) {
            throw new BaseException(ETC_CHANGE_ROLE_CONTENT_CAN_NOT_BE_EMPTY);
        }
        if (etcChangeContent.length() < ETC_CHANGE_ROLE_CONTENT_MINIMUM_LENGTH) {
            throw new BaseException(ETC_CHANGE_ROLE_CONTENT_LENGTH_MUST_GREATER_THAN_MINIMUM_LENGTH);
        }
    }
}
