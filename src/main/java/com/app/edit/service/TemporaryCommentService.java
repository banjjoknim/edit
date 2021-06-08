package com.app.edit.service;

import com.app.edit.config.BaseException;
import com.app.edit.domain.coverletter.CoverLetter;
import com.app.edit.domain.temporarycomment.TemporaryComment;
import com.app.edit.domain.temporarycomment.TemporaryCommentRepository;
import com.app.edit.domain.user.UserInfo;
import com.app.edit.domain.user.UserInfoRepository;
import com.app.edit.enums.State;
import com.app.edit.provider.CoverLetterProvider;
import com.app.edit.provider.TemporaryCommentProvider;
import com.app.edit.request.temporarycomment.PostTemporaryCommentReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.app.edit.config.BaseResponseStatus.*;

@Transactional
@Service
public class TemporaryCommentService {

    private final UserInfoRepository userInfoRepository;
    private final CoverLetterProvider coverLetterProvider;
    private final TemporaryCommentRepository temporaryCommentRepository;
    private final TemporaryCommentProvider temporaryCommentProvider;

    @Autowired
    public TemporaryCommentService(UserInfoRepository userInfoRepository,
                                   CoverLetterProvider coverLetterProvider,
                                   TemporaryCommentRepository temporaryCommentRepository,
                                   TemporaryCommentProvider temporaryCommentProvider) {
        this.userInfoRepository = userInfoRepository;
        this.coverLetterProvider = coverLetterProvider;
        this.temporaryCommentRepository = temporaryCommentRepository;
        this.temporaryCommentProvider = temporaryCommentProvider;
    }

    /**
     * 코멘트 임시 저장
     * @param userInfoId
     * @param parameters
     */
    public void createTemporaryComment(Long userInfoId, PostTemporaryCommentReq parameters) throws BaseException{

        UserInfo userInfo = userInfoRepository.findByStateAndId(State.ACTIVE,userInfoId)
                .orElseThrow(() -> new BaseException(NOT_FOUND_USER));
        CoverLetter coverLetter = coverLetterProvider.getCoverLetterById(parameters.getCoverLetterId());

        TemporaryComment temporaryComment = TemporaryComment.builder()
                .userInfo(userInfo)
                .coverLetter(coverLetter)
                .activity(parameters.getActivity())
                .concretenessLogic(parameters.getConcretenessLogic())
                .sentenceEvaluation(parameters.getSentenceEvaluation())
                .sincerity(parameters.getSincerity())
                .content(parameters.getContent())
                .state(State.ACTIVE)
                .build();

        try{
            temporaryCommentRepository.save(temporaryComment);
        }catch (Exception exception){
            throw new BaseException(FAILED_TO_POST_TEMPORARY_COMMENT);
        }

    }

    /**
     * 임시 코멘트 다시 임시 저장하기
     * @param parameters
     */
    public void updateTemporaryComment(Long temporaryCommentId,PostTemporaryCommentReq parameters) throws BaseException {

        TemporaryComment temporaryComment = temporaryCommentProvider.getTemporaryCommentById(temporaryCommentId);

        temporaryComment.setSentenceEvaluation(parameters.getSentenceEvaluation());
        temporaryComment.setSincerity(parameters.getSincerity());
        temporaryComment.setActivity(parameters.getActivity());
        temporaryComment.setConcretenessLogic(parameters.getConcretenessLogic());
        temporaryComment.setContent(parameters.getContent());

    }

    /**
     * 임시 코멘트 삭제하기
     * @param temporaryCommentId
     */
    public void deleteTemporaryComment(Long temporaryCommentId, Long userInfoId) throws BaseException {
        TemporaryComment temporaryComment = temporaryCommentProvider.getTemporaryCommentById(temporaryCommentId);

        if(!temporaryComment.getUserInfo().getId().equals(userInfoId))
            throw new BaseException(UNAUTHORIZED_AUTHORITY);

        temporaryComment.setState(State.INACTIVE);
    }
}
