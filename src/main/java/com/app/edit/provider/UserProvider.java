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
     * ?????? ?????? ??????
     * @return
     * @throws BaseException
     */
    public List<GetUserRes> retrieveUserList() throws BaseException {

        List<UserInfo> userList;

        // DB??? ???????????? ?????? ?????? ??????
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
     * ?????? ???????????? ????????? ??????
     * @param email
     * @return
     * @throws BaseException
     */
    public UserInfo retrieveUserByEmail(String email) throws BaseException {
        List<UserInfo> existsUserList;

        // DB??? ???????????? email??? ?????? ?????? ??????
        try{
            existsUserList = userInfoRepository.findByStateAndEmailIsContaining(State.ACTIVE,email);
        }catch (Exception e){
            throw new BaseException(FAILED_TO_GET_USER);
        }

        // userList??? ????????? ????????? ????????? ??????
        UserInfo user;
        if (existsUserList != null && existsUserList.size() > 0) {
            user = existsUserList.get(0);
        } else {
            throw new BaseException(NOT_FOUND_USER);
        }

        return user;
    }

    /**
     * ?????????, ????????? ?????? ??????
     * @param email
     * @return
     * @throws BaseException
     */
    public DuplicationCheck checkDuplication(String email, String nickName) throws BaseException{

        List<UserInfo> userInfoList = new LinkedList<>();

        //nickName??? null?????? ????????? ??????
        if(nickName == null){
            userInfoList = userInfoRepository.findByStateAndEmailIsContaining(State.ACTIVE,email);
        }

        //email??? null?????? ????????? ??????
        if(email == null){
            userInfoList = userInfoRepository.findByStateAndNickNameIsContaining(State.ACTIVE,nickName);
        }

        return userInfoList.size() == 0 ?
                DuplicationCheck.builder().duplicationCheck("NO").build() :
                DuplicationCheck.builder().duplicationCheck("YES").build() ;
        
    }

    /**
     * ????????? ??????
     * @param email
     * @throws BaseException
     */
    public void authenticationEmail(String email) throws BaseException {

        String authenticationCode = sesEmailEmailSender.createKey("code");
        ArrayList<String> to = new ArrayList<>();
        to.add(email);
        String subject = "<????????? ??????>";
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
                        "		<span style=\"color: #5a32dc\">????????????</span> ???????????????."																																				+
                        "	</h1>\n"																																																+
                        "	<p style=\"font-size: 16px; line-height: 26px; margin-top: 50px; padding: 0 5px;\">"																													+
                        "		???????????????.<br />"																																													+
                        "		EDIT.??? ????????? ????????? ???????????? ??????????????????.<br />"																																						+
                        "		??????????????? "+ authenticationCode + "?????????<br />"																													+
                        "		???????????????."																																															+
                        "	</p>"																																																	+
                        "	<div style=\"border-top: 1px solid #DDD; padding: 5px;\"></div>"																																		+
                        " </div>"
        );
        emailContent.append("</body>");
        emailContent.append("</html>");

        //???????????? 3??????????????? ??????
        //Date time = Date.from(Instant.now().plusSeconds(180L));
        redisTemplate.opsForValue().set(email,authenticationCode, Duration.ofSeconds(1200L));
        redisTemplate.opsForValue().set(authenticationCode,email, Duration.ofSeconds(1200L));

        // when
        sesEmailEmailSender.send(subject, emailContent.toString(), to);
    }

    /**
     * ????????? ?????? ?????? ??????
     * @param authenticationCode
     * @return
     */
    public AuthenticationCheck authenticationCode(String authenticationCode) throws BaseException {

        //??????????????? ????????? ????????? ??????
        String email = redisTemplate.opsForValue().get(authenticationCode);

        //???????????? ??????????????? ????????? ?????? ??????
        if(email == null)
            throw new BaseException(FAILED_TO_AUTHENTICATION_CODE);

        //???????????? ???????????? ?????? ?????? ??????
        String authentication = redisTemplate.opsForValue().get(email);

        Long expireTime = redisTemplate.getExpire(email);
        //??????????????? ????????? ??????
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

        //?????? ????????? ?????? ?????? ??????????????? true
        //if(currentTime.isBefore(parsedTime))
        //else
         //   throw new BaseException(AUTHENTICATION_TIME_EXPIRED);
    }

    /**
     * ?????????
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
     * ????????????
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
     * ????????? ??????
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
     * ???????????? ??????
     * YES??? ?????????, NO??? ?????? ??????
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
     * ??? ????????? ??????
     * @param
     * @return
     */
    public GetProfileRes retrieveProfile(Long userInfoId) throws BaseException{

        return userInfoRepository.findProfileByUser(userInfoId,State.ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_FOUND_USER));
    }

    /**
     * ?????? ?????? ?????? ??????
     * @param userId
     * @return
     */
    public GetAuthenticationRes AuthenticationMentor(Long userId) throws BaseException{

        UserInfo userInfo = userInfoRepository.findByStateAndId(State.ACTIVE,userId)
                 .orElseThrow(() -> new BaseException(FAILED_TO_GET_USER));

        //IsProcessing = no -> ??????????????? Waiting ??????
        Optional<CertificationRequest> certificationRequest1 =
                certificationRequestRepository.findTop1ByIsProcessingAndUserInfoOrderByCreatedAtDesc(IsProcessing.NO,userInfo);

        if(certificationRequest1.isPresent())
            return GetAuthenticationRes.builder()
                    .presentState(AuthenticationCheck.WAITING.name())
                    .build();

        //IsProcessing = yes -> ?????? ?????? = YES, ?????? ?????? = NO
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
     * ????????? ??????
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
     * ?????? ??????
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
     * ??? ?????? ??????
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
     * ????????? ????????? ????????? ??????
     * @param userInfoId
     * @return
     */
    public GetUserInfosRes retrieveSympathizeUser(Long userInfoId) throws BaseException {

        return userInfoRepository.findProfileBySympathizeUser(userInfoId,State.ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_FOUND_USER));
    }
}
