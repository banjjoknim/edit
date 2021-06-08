package com.app.edit.provider;

import com.app.edit.config.BaseException;
import com.app.edit.config.secret.Secret;
import com.app.edit.domain.certificationRequest.CertificationRequest;
import com.app.edit.domain.certificationRequest.CertificationRequestRepository;
import com.app.edit.domain.user.UserInfo;
import com.app.edit.domain.user.UserInfoRepository;
import com.app.edit.enums.*;
import com.app.edit.request.user.PostLoginReq;
import com.app.edit.response.user.*;
import com.app.edit.service.EmailSenderService;
import com.app.edit.utils.AES128;
import com.app.edit.utils.GetDateTime;
import com.app.edit.utils.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.app.edit.config.BaseResponseStatus.*;

@Service
@Slf4j
@Transactional(readOnly = true)
public class UserProvider {

    private final UserInfoRepository userInfoRepository;
    private final EmailSenderService sesEmailEmailSender;
    private final GetDateTime getDateTime;
    private final HashMap<String,String> authenticationCodeRepository;
    private final CertificationRequestRepository certificationRequestRepository;
    private final JwtService jwtService;
    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public UserProvider(UserInfoRepository userRepository,
                        EmailSenderService sesEmailEmailSender,
                        GetDateTime getDateTime, HashMap<String, String> authenticationCodeRepository,
                        CertificationRequestRepository certificationRequestRepository, JwtService jwtService, RedisTemplate<String, String> redisTemplate) {
        this.userInfoRepository = userRepository;
        this.sesEmailEmailSender = sesEmailEmailSender;
        this.getDateTime = getDateTime;
        this.authenticationCodeRepository = authenticationCodeRepository;
        this.certificationRequestRepository = certificationRequestRepository;
        this.jwtService = jwtService;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 전체 회원 조회
     * @return
     * @throws BaseException
     */
    public List<GetUserRes> retrieveUserList() throws BaseException {

        List<UserInfo> userList;

        // DB에 접근해서 전체 회원 조회
        try{
            userList = userInfoRepository.findByState(State.ACTIVE);
        }catch (Exception e){
            throw new BaseException(FAILED_TO_GET_USER);
        }

        return userList.stream()
                .map(user -> GetUserRes.builder()
                        .name(user.getName())
                        .nickname(user.getNickName())
                        .phoneNumber(user.getPhoneNumber())
                        .etcJobName(user.getEtcJobName())
                        .email(user.getEmail())
                        .withdrawalContent(user.getWithdrawalContent())
                        .etcWithdrawalContent(user.getEtcWithdrawalContent())
                        .coinCount(user.getCoinCount())
                        .colorName(user.getUserProfile().getProfileColor().getName())
                        .emotionName(user.getUserProfile().getProfileEmotion().getName())
                        .jobName(user.getJob().getName())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 중복 이메일이 있는지 검사
     * @param email
     * @return
     * @throws BaseException
     */
    public UserInfo retrieveUserByEmail(String email) throws BaseException {
        List<UserInfo> existsUserList;

        // DB에 접근해서 email로 회원 정보 조회
        try{
            existsUserList = userInfoRepository.findByStateAndEmailIsContaining(State.ACTIVE,email);
        }catch (Exception e){
            throw new BaseException(FAILED_TO_GET_USER);
        }

        // userList에 중복된 회원이 있는지 검사
        UserInfo user;
        if (existsUserList != null && existsUserList.size() > 0) {
            user = existsUserList.get(0);
        } else {
            throw new BaseException(NOT_FOUND_USER);
        }

        return user;
    }

    /**
     * 이메일, 닉네임 중복 검사
     * @param email
     * @return
     * @throws BaseException
     */
    public DuplicationCheck checkDuplication(String email, String nickName) throws BaseException{

        List<UserInfo> userInfoList = new LinkedList<>();

        //nickName이 null이면 이메일 검증
        if(nickName == null){
            userInfoList = userInfoRepository.findByStateAndEmailIsContaining(State.ACTIVE,email);
        }

        //email이 null이면 닉네임 검증
        if(email == null){
            userInfoList = userInfoRepository.findByStateAndNickNameIsContaining(State.ACTIVE,nickName);
        }

        return userInfoList.size() == 0 ?
                DuplicationCheck.builder().duplicationCheck("NO").build() :
                DuplicationCheck.builder().duplicationCheck("YES").build() ;
        
    }

    /**
     * 이메일 인증
     * @param email
     * @throws BaseException
     */
    public void authenticationEmail(String email) throws BaseException {

        String authenticationCode = sesEmailEmailSender.createKey("code");
        ArrayList<String> to = new ArrayList<>();
        to.add(email);
        String subject = "<이메일 인증>";
        StringBuilder emailContent = new StringBuilder();
        emailContent.append("<!DOCTYPE html>");
        emailContent.append("<html>");
        emailContent.append("<head>");
        emailContent.append("</head>");
        emailContent.append("<body>");
        emailContent.append(
                " <div" 																																																	+
                        "	style=\"font-family: 'Apple SD Gothic Neo', 'sans-serif' !important; width: 400px; height: 600px; border-top: 4px solid #5a32dc; margin: 100px auto; padding: 30px 0; box-sizing: border-box;\">"		+
                        "	<h1 style=\"margin: 0; padding: 0 5px; font-size: 28px; font-weight: 400;\">"																															+
                        "		<span style=\"font-size: 15px; margin: 0 0 10px 3px;\">EDIT.</span><br />"																													+
                        "		<span style=\"color: #5a32dc\">메일인증</span> 안내입니다."																																				+
                        "	</h1>\n"																																																+
                        "	<p style=\"font-size: 16px; line-height: 26px; margin-top: 50px; padding: 0 5px;\">"																													+
                        "		안녕하세요.<br />"																																													+
                        "		EDIT.에 가입해 주셔서 진심으로 감사드립니다.<br />"																																						+
                        "		인증코드는 "+ authenticationCode + "입니다<br />"																													+
                        "		감사합니다."																																															+
                        "	</p>"																																																	+
                        "	<div style=\"border-top: 1px solid #DDD; padding: 5px;\"></div>"																																		+
                        " </div>"
        );
        emailContent.append("</body>");
        emailContent.append("</html>");

        //인증코드 3분제한으로 저장
        //Date time = Date.from(Instant.now().plusSeconds(180L));
        redisTemplate.opsForValue().set(email,authenticationCode, Duration.ofSeconds(1200L));
        redisTemplate.opsForValue().set(authenticationCode,email, Duration.ofSeconds(1200L));

        // when
        sesEmailEmailSender.send(subject, emailContent.toString(), to);
    }

    /**
     * 이메일 인증 코드 검증
     * @param authenticationCode
     * @return
     */
    public AuthenticationCheck authenticationCode(String authenticationCode) throws BaseException {

        //인증코드를 통해서 이메일 조회
        String email = redisTemplate.opsForValue().get(authenticationCode);

        //이메일이 비어있으면 잘못된 인증 코드
        if(email == null)
            throw new BaseException(FAILED_TO_AUTHENTICATION_CODE);

        //이메일로 최신화된 인증 코드 조회
        String authentication = redisTemplate.opsForValue().get(email);

        Long expireTime = redisTemplate.getExpire(email);
        //인증코드가 없다면 에러
       if(authentication == null || expireTime == null)
            throw new BaseException(FAILED_TO_AUTHENTICATION_CODE);

        if(authentication.equals(authenticationCode)) {
            if(expireTime > 900L) {
                return AuthenticationCheck.YES;
            }else if(0L <= expireTime){
                return AuthenticationCheck.NO;
            }else{
                throw new BaseException(FAILED_TO_AUTHENTICATION_CODE);
            }
        }
        else {
            throw new BaseException(FAILED_TO_AUTHENTICATION_CODE);
        }

        //LocalDateTime currentTime = LocalDateTime.now();
        //LocalDateTime parsedTime = LocalDateTime.parse(authenticationTime, getDateTime.GetFormatter());

        //현재 시간이 인증 시간 이전이라면 true
        //if(currentTime.isBefore(parsedTime))
        //else
         //   throw new BaseException(AUTHENTICATION_TIME_EXPIRED);
    }

    /**
     * 로그인
     * @return
     */
    public PostUserRes login(PostLoginReq parameters) throws BaseException{

        String EncodingPassword;
        try {
            EncodingPassword = new AES128(Secret.USER_INFO_PASSWORD_KEY).encrypt(parameters.getPassword());
        }catch (Exception ignored) {
            throw new BaseException(FAILED_TO_ENCRYPT_PASSWORD);
        }

        UserInfo user = userInfoRepository.findByStateAndEmailAndPassword(State.ACTIVE, parameters.getEmail(), EncodingPassword)
                .orElseThrow(() -> new BaseException(FAILED_TO_LOGIN));

        return PostUserRes.builder()
                .jwt(jwtService.createJwt(user.getId(),user.getUserRole()))
                .userRole(user.getUserRole())
                .isCertificatedMentor(user.getIsCertificatedMentor().equals(AuthenticationCheck.YES))
                .build();

    }

    /**
     * 로그아웃
     * @return
     */
    public void logout() throws BaseException {

        String accessToken = jwtService.getJwt();

        if(redisTemplate.opsForValue().get(accessToken) != null){
            throw new BaseException(ALREADY_LOGOUT);
        }

        //StringBuilder sb = new StringBuilder();
        //String today = getDateTime.getToday();
        //sb.append("blacklist:").append(today);
        //String setDays = sb.toString();
        //redisTemplate.opsForSet().add(setDays, accessToken);

        Date expirationDate = jwtService.getExpireDate(accessToken);
        redisTemplate.opsForValue().set(
                accessToken,"edit",
                expirationDate.getTime() - System.currentTimeMillis(), TimeUnit.MILLISECONDS
        );
    }

    /**
     * 이메일 찾기
     * @param name
     * @param phoneNumber
     * @return
     */
    public GetEmailRes searchEmail(String name, String phoneNumber) throws BaseException{

        UserInfo userInfo = userInfoRepository.findByStateAndNameAndPhoneNumber(State.ACTIVE,name,phoneNumber)
                .orElseThrow(() -> new BaseException(FAILED_TO_GET_USER));

        String email = userInfo.getEmail();
        StringBuilder sb = new StringBuilder();
        sb.append(email);
        int index = email.indexOf('@');
        for(int i = index - (index / 3); i < index ; i++)
            sb.replace(i,i + 1, "*");
        return GetEmailRes.builder()
                .email(sb.toString())
                .build();
    }

    /**
     * 비밀번호 인증
     * YES가 인증됨, NO는 인증 안됨
     * @param password
     * @return
     */
    public AuthenticationCheck authenticationPassword(Long userId,String password) throws BaseException{

        UserInfo userInfo = userInfoRepository.findByStateAndId(State.ACTIVE,userId)
                .orElseThrow(() -> new BaseException(FAILED_TO_GET_USER));

        String encodingPassword;
        try{
            encodingPassword = new AES128(Secret.USER_INFO_PASSWORD_KEY).encrypt(password);
        }catch (Exception ignore){
            throw new BaseException(FAILED_TO_ENCRYPT_PASSWORD);
        }

        if(userInfo.getPassword().equals(encodingPassword))
            return AuthenticationCheck.YES;
        else
            return AuthenticationCheck.NO;
    }

    /**
     * 내 프로필 조회
     * @param
     * @return
     */
    public GetProfileRes retrieveProfile(Long userInfoId) throws BaseException{

        return userInfoRepository.findProfileByUser(userInfoId,State.ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_FOUND_USER));
    }

    /**
     * 현재 인증 상태 조회
     * @param userId
     * @return
     */
    public GetAuthenticationRes AuthenticationMentor(Long userId) throws BaseException{

        UserInfo userInfo = userInfoRepository.findByStateAndId(State.ACTIVE,userId)
                 .orElseThrow(() -> new BaseException(FAILED_TO_GET_USER));

        //IsProcessing = no -> 처리중이면 Waiting 반환
        Optional<CertificationRequest> certificationRequest1 =
                certificationRequestRepository.findTop1ByIsProcessingAndUserInfoOrderByCreatedAtDesc(IsProcessing.NO,userInfo);

        if(certificationRequest1.isPresent())
            return GetAuthenticationRes.builder()
                    .presentState(AuthenticationCheck.WAITING.name())
                    .build();

        //IsProcessing = yes -> 인증 성공 = YES, 인증 실패 = NO
        Optional<CertificationRequest> certificationRequest =
                certificationRequestRepository.findTop1ByIsProcessingAndUserInfoOrderByCreatedAtDesc(IsProcessing.YES,userInfo);

        if(certificationRequest.isPresent()) {
            if(userInfo.getIsCertificatedMentor().equals(AuthenticationCheck.YES)) {
                return GetAuthenticationRes.builder()
                        .presentState(AuthenticationCheck.YES.name())
                        .build();
            }else{
                return GetAuthenticationRes.builder()
                        .presentState(AuthenticationCheck.NO.name())
                        .build();
            }
        }
        throw new BaseException(FAILED_TO_GET_CERTIFICATION_REQUEST);
    }

    /**
     * 닉네임 조회
     * @param userId
     * @return
     */
    public GetNickNameRes retrieveNickName(Long userId) throws BaseException{

        return  GetNickNameRes.builder()
                .nickName(userInfoRepository.findByStateAndId(State.ACTIVE, userId)
                        .orElseThrow(() -> new BaseException(FAILED_TO_GET_USER)).getName())
                .build();

    }

    /**
     * 역할 조회
     * @param userId
     * @return
     */
    public GetRoleRes retrieveRole(Long userId) throws BaseException{

        UserInfo userInfo = userInfoRepository.findByStateAndId(State.ACTIVE, userId)
                .orElseThrow(() -> new BaseException(FAILED_TO_GET_USER));

        return GetRoleRes.builder()
                .nickName(userInfo.getNickName())
                .userRole(userInfo.getUserRole())
                .build();
    }

    /**
     * 내 코인 조회
     * @param userId
     * @return
     */
    public GetCoinRes retrieveCoin(Long userId) throws BaseException{

        UserInfo userInfo = userInfoRepository.findByStateAndId(State.ACTIVE, userId)
                .orElseThrow(() -> new BaseException(FAILED_TO_GET_USER));

        return GetCoinRes.builder()
                .coinCount(userInfo.getCoinCount())
                .appreciateCount((long) userInfo.getAppreciates().size())
                .adoptCount(userInfo.getComments().stream()
                        .filter(comment -> comment.getIsAdopted().equals(IsAdopted.YES)).count())
                .build();
    }

    /**
     * 공감한 자소서 이용자 정보
     * @param userInfoId
     * @return
     */
    public GetUserInfosRes retrieveSympathizeUser(Long userInfoId) throws BaseException {

        return userInfoRepository.findProfileBySympathizeUser(userInfoId,State.ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_FOUND_USER));
    }
}
