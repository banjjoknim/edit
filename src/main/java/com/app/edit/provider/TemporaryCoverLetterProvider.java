package com.app.edit.provider;

import com.app.edit.config.BaseException;
import com.app.edit.domain.comment.Comment;
import com.app.edit.domain.coverletter.CoverLetter;
import com.app.edit.domain.temporarycoverletter.TemporaryCoverLetter;
import com.app.edit.domain.temporarycoverletter.TemporaryCoverLetterRepository;
import com.app.edit.domain.user.UserInfo;
import com.app.edit.enums.CoverLetterType;
import com.app.edit.enums.IsAdopted;
import com.app.edit.enums.State;
import com.app.edit.response.temporarycoverletter.GetCompletingTemporaryCoverLetterRes;
import com.app.edit.response.temporarycoverletter.GetWritingTemporaryCoverLetterRes;
import com.app.edit.response.temporarycoverletter.GetTemporaryCoverLettersRes;
import com.app.edit.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.app.edit.config.BaseResponseStatus.*;
import static java.util.stream.Collectors.toList;

@Transactional(readOnly = true)
@Service
public class TemporaryCoverLetterProvider {

    private final TemporaryCoverLetterRepository temporaryCoverLetterRepository;
    private final UserInfoProvider userInfoProvider;
    private final CoverLetterProvider coverLetterProvider;
    private final CommentProvider commentProvider;
    private final JwtService jwtService;

    @Autowired
    public TemporaryCoverLetterProvider(TemporaryCoverLetterRepository temporaryCoverLetterRepository,
                                        UserInfoProvider userInfoProvider, CoverLetterProvider coverLetterProvider,
                                        CommentProvider commentProvider, JwtService jwtService) {
        this.temporaryCoverLetterRepository = temporaryCoverLetterRepository;
        this.userInfoProvider = userInfoProvider;
        this.coverLetterProvider = coverLetterProvider;
        this.commentProvider = commentProvider;
        this.jwtService = jwtService;
    }

    /**
     * 임시 저장한 자소서 조회
     *
     * @param pageable
     * @param coverLetterType
     * @return
     * @throws BaseException
     */
    public List<GetTemporaryCoverLettersRes> retrieveTemporaryCoverLetters(Pageable pageable, CoverLetterType coverLetterType) throws BaseException {
        Long userInfoId = jwtService.getUserInfo().getUserId();
        UserInfo userInfo = userInfoProvider.getUserInfoById(userInfoId);
        Page<TemporaryCoverLetter> temporaryCoverLetters = temporaryCoverLetterRepository
                .findTemporaryCoverLetters(pageable, userInfoId, State.ACTIVE, coverLetterType);
        return getTemporaryCoverLettersResponses(userInfo, temporaryCoverLetters);
    }

    private List<GetTemporaryCoverLettersRes> getTemporaryCoverLettersResponses(UserInfo userInfo, Page<TemporaryCoverLetter> temporaryCoverLetters) {
        return temporaryCoverLetters.stream()
                .map(temporaryCoverLetter -> {
                    Long temporaryCoverLetterId = temporaryCoverLetter.getId();
                    String nickName = userInfo.getNickName();
                    String jobName = userInfo.getJob().getName();
                    String coverLetterCategoryName = temporaryCoverLetter.getCoverLetterCategory().getName();
                    String temporaryCoverLetterContent = temporaryCoverLetter.getContent();
                    return new GetTemporaryCoverLettersRes(temporaryCoverLetterId, nickName,
                            jobName, coverLetterCategoryName, temporaryCoverLetterContent);
                })
                .collect(toList());
    }

    public TemporaryCoverLetter getTemporaryCoverLetterById(Long temporaryCoverLetterId) throws BaseException {
        Optional<TemporaryCoverLetter> selectedTemporaryCoverLetter = temporaryCoverLetterRepository
                .findById(temporaryCoverLetterId);
        if (selectedTemporaryCoverLetter.isEmpty()) {
            throw new BaseException(NOT_FOUND_TEMPORARY_COVER_LETTER);
        }
        if (selectedTemporaryCoverLetter.get().getState().equals(State.INACTIVE)) {
            throw new BaseException(ALREADY_DELETED_TEMPORARY_COVER_LETTER);
        }
        return selectedTemporaryCoverLetter.get();
    }

    public GetWritingTemporaryCoverLetterRes retrieveWritingTemporaryCoverLetter(Long temporaryCoverLetterId) throws BaseException {
        Long userInfoId = jwtService.getUserInfo().getUserId();
        TemporaryCoverLetter temporaryCoverLetter = getTemporaryCoverLetterById(temporaryCoverLetterId);
        UserInfo userInfo = userInfoProvider.getUserInfoById(userInfoId);
        if (!temporaryCoverLetter.getUserInfo().equals(userInfo)) {
            throw new BaseException(DO_NOT_HAVE_PERMISSION);
        }
        if (temporaryCoverLetter.getType().equals(CoverLetterType.COMPLETING)) {
            throw new BaseException(FOUND_COVER_LETTER_TYPE_IS_NOT_WRITING);
        }
        Long selectedTemporaryCoverLetterId = temporaryCoverLetter.getId();
        Long coverLetterCategoryId = temporaryCoverLetter.getCoverLetterCategory().getId();
        String content = temporaryCoverLetter.getContent();
        return new GetWritingTemporaryCoverLetterRes(selectedTemporaryCoverLetterId, coverLetterCategoryId, content);
    }

    public GetCompletingTemporaryCoverLetterRes retrieveCompletingTemporaryCoverLetter(Long temporaryCoverLetterId) throws BaseException {
        Long userInfoId = jwtService.getUserInfo().getUserId();
        UserInfo userInfo = userInfoProvider.getUserInfoById(userInfoId);
        TemporaryCoverLetter temporaryCoverLetter = getTemporaryCoverLetterById(temporaryCoverLetterId);
        if (!temporaryCoverLetter.getUserInfo().equals(userInfo)) {
            throw new BaseException(DO_NOT_HAVE_PERMISSION);
        }
        if (temporaryCoverLetter.getType().equals(CoverLetterType.WRITING)) {
            throw new BaseException(FOUND_COVER_LETTER_TYPE_IS_NOT_COMPLETING);
        }
        Long originalCoverLetterId = temporaryCoverLetter.getOriginalCoverLetterId();
        CoverLetter originalCoverLetter = coverLetterProvider.getCoverLetterById(originalCoverLetterId);
        Comment adoptedComment = originalCoverLetter.getAdoptedComment();

        Long originalCoverLetterCategoryId = originalCoverLetter.getCoverLetterCategory().getId();
        String originalCoverLetterContent = originalCoverLetter.getContent();
        String adoptedCommentContent = adoptedComment.getContent();
        String temporaryCoverLetterContent = temporaryCoverLetter.getContent();
        return new GetCompletingTemporaryCoverLetterRes(temporaryCoverLetterId, originalCoverLetterCategoryId,
                originalCoverLetterContent, adoptedCommentContent, temporaryCoverLetterContent);
    }
}
