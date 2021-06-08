package com.app.edit.service;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.SendEmailResult;
import com.app.edit.config.BaseException;
import com.app.edit.request.user.EmailRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

import static com.app.edit.config.BaseResponseStatus.EMPTY_RECEIVER;
import static com.app.edit.config.BaseResponseStatus.FAILED_TO_SEND_EMAIL;

@Slf4j
@Service
public class EmailSenderService {

    private final AmazonSimpleEmailService amazonSimpleEmailService;

    @Value("${ses.email}")
    private String from;

    public EmailSenderService(AmazonSimpleEmailService amazonSimpleEmailService) {
        this.amazonSimpleEmailService = amazonSimpleEmailService;
    }

    /**
     * 이메일 전송
     */
    public void send(String subject, String content, List<String> receivers) throws BaseException {
        if(receivers.size() == 0) {
            log.error("메일을 전송할 대상이 없습니다: [{}]", subject);
            throw new BaseException(EMPTY_RECEIVER);
        }

        EmailRequest senderDto = EmailRequest.builder()
                .from(from)
                .to(receivers)
                .subject(subject)
                .content(content)
                .build();

        SendEmailResult sendEmailResult = amazonSimpleEmailService.sendEmail(senderDto.toSendRequestDto());

        if(sendEmailResult.getSdkHttpMetadata().getHttpStatusCode() == 200) {
            log.info("[AWS SES] 메일전송완료 => " + senderDto.getTo());
        }else {
            log.error("[AWS SES] 메일전송 중 에러가 발생했습니다: {}", sendEmailResult.getSdkResponseMetadata().toString());
            log.error("발송실패 대상자: " + senderDto.getTo() + " / subject: " + senderDto.getSubject());
            throw new BaseException(FAILED_TO_SEND_EMAIL);
        }
    }

    public String createKey(String type) {
        StringBuilder key = new StringBuilder();
        Random rnd = new Random();

        int size = type.equals("code") ? 6 : 10;

        for (int i = 0; i < size; i++) { // 인증코드 6자리
            int index = rnd.nextInt(3); // 0~2 까지 랜덤

            switch (index) {
                case 0:
                    key.append((char) ((int) (rnd.nextInt(26)) + 97));
                    //  a~z  (ex. 1+97=98 => (char)98 = 'b')
                    break;
                case 1:
                    key.append((char) ((int) (rnd.nextInt(26)) + 65));
                    //  A~Z
                    break;
                case 2:
                    key.append((rnd.nextInt(10)));
                    // 0~9
                    break;
            }
        }

        return key.toString();
    }



}