package com.app.edit.provider;

import com.app.edit.config.BaseException;
import com.app.edit.domain.temporarycomment.TemporaryComment;
import com.app.edit.domain.temporarycomment.TemporaryCommentRepository;
import com.app.edit.enums.State;
import com.app.edit.response.comment.GetMyCommentRes;
import com.app.edit.response.comment.GetMyCommentsRes;
import com.app.edit.response.coverletter.GetCoverLettersByCommentRes;
import com.app.edit.response.temporaryComment.GetTemporaryCommentRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.app.edit.config.BaseResponseStatus.NOT_FOUND_TEMPORARY_COMMENT;
import static com.app.edit.config.BaseResponseStatus.UNAUTHORIZED_AUTHORITY;

@Service
@Transactional(readOnly = true)
public class TemporaryCommentProvider {

    private final CoverLetterProvider coverLetterProvider;
    private final TemporaryCommentRepository temporaryCommentRepository;
    private final UserProvider userProvider;

    @Autowired
    public TemporaryCommentProvider(CoverLetterProvider coverLetterProvider, TemporaryCommentRepository temporaryCommentRepository,
                                    UserProvider userProvider) {
        this.coverLetterProvider = coverLetterProvider;
        this.temporaryCommentRepository = temporaryCommentRepository;
        this.userProvider = userProvider;
    }

    /**
     * 내 임시 코멘트 조회
     * @param userInfoId
     * @return
     */
    public List<GetMyCommentsRes> getMyTemporaryComments(Long userInfoId) throws BaseException {

        List<GetMyCommentRes> getMyCommentResList =
                temporaryCommentRepository.findMyTemporaryComments(userInfoId, State.ACTIVE);

        if(getMyCommentResList.size() == 0)
            throw new BaseException(NOT_FOUND_TEMPORARY_COMMENT);

        return getMyCommentResList.stream()
                .map(getMyCommentRes -> {
                    try {
                        return GetMyCommentsRes.builder()
                                .commentInfo(getMyCommentRes)
                                .userInfo(userProvider.retrieveSympathizeUser(userInfoId))
                                .build();
                    } catch (BaseException baseException) {
                       return null;
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * 코멘트 이어서 작성하기
     * @param temporaryCommentId
     * @param userInfoId
     * @return
     */
    public GetTemporaryCommentRes getMyTemporaryComment(Long temporaryCommentId, Long userInfoId) throws BaseException {

        GetMyCommentRes getMyCommentRes =
                temporaryCommentRepository.findMyTemporaryComment(temporaryCommentId,State.ACTIVE);

        TemporaryComment temporaryComment = getTemporaryCommentById(temporaryCommentId);

        if(!temporaryComment.getUserInfo().getId().equals(userInfoId))
            throw new BaseException(UNAUTHORIZED_AUTHORITY);

        return GetTemporaryCommentRes.builder()
                .getCoverLettersByTemporaryCommentRes(temporaryCommentToGetCoverLettersByCommentRes(temporaryComment,userInfoId))
                .getMyTemporaryCommentRes(getMyCommentRes)
                .build();
    }

    public TemporaryComment getTemporaryCommentById(Long temporaryCommentId) throws BaseException{
        return temporaryCommentRepository.findByIdAndState(temporaryCommentId,State.ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_FOUND_TEMPORARY_COMMENT));
    }

    public GetCoverLettersByCommentRes temporaryCommentToGetCoverLettersByCommentRes
            (TemporaryComment temporaryComment, Long userInfoId) throws BaseException {
        return GetCoverLettersByCommentRes.builder()
                .userInfo(userProvider.retrieveSympathizeUser(userInfoId))
                .coverLetterId(temporaryComment.getId())
                .coverLetterContent(temporaryComment.getContent())
                .coverLetterCategoryName(null)
                .build();
    }

    public List<TemporaryComment> getTemporaryCommentByUserInfoIdAndStatus(Long userId, Long coverLetterId) {
        return temporaryCommentRepository.findByUserAndCoverLetterAndStatus(userId,coverLetterId,State.ACTIVE);
    }
}
