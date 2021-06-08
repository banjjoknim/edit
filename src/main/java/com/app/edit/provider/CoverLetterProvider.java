package com.app.edit.provider;

import com.app.edit.config.BaseException;
import com.app.edit.config.BaseResponseStatus;
import com.app.edit.config.PageRequest;
import com.app.edit.domain.comment.Comment;
import com.app.edit.domain.coverletter.CoverLetter;
import com.app.edit.domain.coverletter.CoverLetterRepository;
import com.app.edit.domain.sympathy.Sympathy;
import com.app.edit.domain.sympathy.SympathyRepository;
import com.app.edit.domain.user.UserInfo;
import com.app.edit.enums.CoverLetterType;
import com.app.edit.enums.IsAdopted;
import com.app.edit.enums.State;
import com.app.edit.response.coverletter.GetCoverLetterToCompleteRes;
import com.app.edit.response.coverletter.GetCoverLettersForLimitScrollRes;
import com.app.edit.response.coverletter.GetCoverLettersRes;
import com.app.edit.response.coverletter.GetMainCoverLettersRes;
import com.app.edit.response.sympathize.GetSympathizeCoverLetterRes;
import com.app.edit.response.sympathize.GetSympathizeCoverLettersRes;
import com.app.edit.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static com.app.edit.config.BaseResponseStatus.FAILED_TO_GET_SYMPATHIES_COVERLETTER;
import static com.app.edit.config.BaseResponseStatus.NOT_FOUND_COVER_LETTER;
import static com.app.edit.config.Constant.*;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

@Transactional(readOnly = true)
@Service
public class CoverLetterProvider {

    private final CoverLetterRepository coverLetterRepository;
    private final SympathyProvider sympathyProvider;
    private final JwtService jwtService;
    private final SympathyRepository sympathyRepository;
    private final UserProvider userProvider;


    @Autowired
    public CoverLetterProvider(CoverLetterRepository coverLetterRepository, SympathyProvider sympathyProvider,
                               SympathyRepository sympathyRepository, UserProvider userProvider,
                               JwtService jwtService) {
        this.coverLetterRepository = coverLetterRepository;
        this.sympathyProvider = sympathyProvider;
        this.sympathyRepository = sympathyRepository;
        this.userProvider = userProvider;
        this.jwtService = jwtService;

    }

    /*
     * 메인 페이지 자소서 조회
     **/
    public GetMainCoverLettersRes retrieveMainCoverLetters() throws BaseException {
        Pageable pageableForToday = PageRequest.of(ONE, MAIN_TODAY_COVER_LETTERS_COUNT);
        Pageable pageableForAnother = PageRequest.of(ONE, MAIN_ANOTHER_COVER_LETTERS_COUNT);
        return new GetMainCoverLettersRes(retrieveTodayCoverLetters(pageableForToday).getCoverLetters(),
                retrieveWaitingForCommentCoverLetters(pageableForAnother).getCoverLetters(),
                retrieveAdoptedCoverLetters(pageableForAnother).getCoverLetters(),
                retrieveManySympathiesCoverLetters(pageableForAnother).getCoverLetters());
    }

    /*
     * 오늘의 문장 조회
     **/
    public GetCoverLettersForLimitScrollRes retrieveTodayCoverLetters(Pageable pageable) throws BaseException {
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        LocalDateTime startOfTomorrow = startOfToday.plusDays(ONE);
        Page<CoverLetter> coverLettersOnToday = coverLetterRepository
                .findCoverLettersOnToday(pageable, startOfToday, startOfTomorrow, State.ACTIVE, CoverLetterType.WRITING);
        return getCoverLettersWithLimitScroll(coverLettersOnToday);
    }

    /**
     * 스크롤 적용 응답으로 변환
     * @param coverLetterPage
     * @return
     * @throws BaseException
     */
    private GetCoverLettersForLimitScrollRes getCoverLettersWithLimitScroll(Page<CoverLetter> coverLetterPage) throws BaseException {
        List<GetCoverLettersRes> coverLetters = getCoverLettersResponses(coverLetterPage);
        return new GetCoverLettersForLimitScrollRes(coverLetters, coverLetterPage.getTotalElements(), coverLetterPage.hasNext());
    }

    /*
     * 코멘트를 기다리고 있어요 조회
     **/
    public GetCoverLettersForLimitScrollRes retrieveWaitingForCommentCoverLetters(Pageable pageable) throws BaseException {
        Page<CoverLetter> coverLettersHasNotComment = coverLetterRepository.findCoverLettersHasNotComment(pageable, State.ACTIVE, CoverLetterType.WRITING);
        return getCoverLettersWithLimitScroll(coverLettersHasNotComment);
    }

    /*
     * 채택이 완료되었어요 조회
     **/
    public GetCoverLettersForLimitScrollRes retrieveAdoptedCoverLetters(Pageable pageable) throws BaseException {
        Page<CoverLetter> coverLettersHasAdoptedComment = coverLetterRepository.findCoverLettersHasAdoptedComment(pageable, IsAdopted.YES, State.ACTIVE, CoverLetterType.WRITING);
        return getCoverLettersWithLimitScroll(coverLettersHasAdoptedComment);
    }

    /*
     * 많은 분들이 공감하고 있어요 조회
     **/
    public GetCoverLettersForLimitScrollRes retrieveManySympathiesCoverLetters(Pageable pageable) throws BaseException {
        LocalDateTime beforeThreeDays = LocalDateTime.now().minusDays(CAN_STAY_DAY);
        Page<CoverLetter> coverLettersHasManySympathies = coverLetterRepository.findCoverLettersHasManySympathies(pageable, beforeThreeDays, State.ACTIVE, CoverLetterType.WRITING);
        List<GetCoverLettersRes> coverLetters = getCoverLettersResponses(coverLettersHasManySympathies).stream()
                .sorted(comparing(GetCoverLettersRes::getSympathiesCount).reversed())
                .collect(toList());
        return new GetCoverLettersForLimitScrollRes(coverLetters, coverLettersHasManySympathies.getTotalElements(), coverLettersHasManySympathies.hasNext());
    }

    private List<GetCoverLettersRes> getCoverLettersResponses(Page<CoverLetter> coverLetterPage) throws BaseException {
        Long userInfoId = jwtService.getUserInfo().getUserId();
        return coverLetterPage.stream()
                .map(coverLetter -> {
                    GetCoverLettersRes getCoverLettersRes = coverLetter.toGetCoverLetterRes();
                    setUserProfileToRes(coverLetter, getCoverLettersRes);
                    setIsSympathyToRes(userInfoId, coverLetter, getCoverLettersRes);
                    setIsMineToRes(userInfoId, coverLetter, getCoverLettersRes);
                    setSympathiesCountToRes(coverLetter, getCoverLettersRes);
                    return getCoverLettersRes;
                })
                .collect(toList());
    }

    private void setSympathiesCountToRes(CoverLetter coverLetter, GetCoverLettersRes getCoverLettersRes) {
        Long sympathiesCount = sympathyProvider.getSympathiesCount(coverLetter);
        getCoverLettersRes.setSympathiesCount(sympathiesCount);
    }

    private void setIsMineToRes(Long userInfoId, CoverLetter coverLetter, GetCoverLettersRes getCoverLettersRes) {
        boolean isMine = coverLetter.getUserInfo().getId().equals(userInfoId);
        getCoverLettersRes.setIsMine(isMine);
    }

    private void setIsSympathyToRes(Long userInfoId, CoverLetter coverLetter, GetCoverLettersRes getCoverLettersRes) {
        boolean isSympathy = sympathyProvider.getIsSympathy(coverLetter.getId(), userInfoId);
        getCoverLettersRes.setIsSympathy(isSympathy);
    }

    private void setUserProfileToRes(CoverLetter coverLetter, GetCoverLettersRes getCoverLettersRes) {
        String profileColorName = coverLetter.getUserInfo().getUserProfile().getProfileColor().getName();
        String profileEmotionName = coverLetter.getUserInfo().getUserProfile().getProfileEmotion().getName();
        getCoverLettersRes.setUserProfile(profileColorName + PROFILE_SEPARATOR + profileEmotionName);
    }

    public CoverLetter getCoverLetterById(Long coverLetterId) throws BaseException {
        Optional<CoverLetter> coverLetter = coverLetterRepository.findById(coverLetterId);
        if (coverLetter.isEmpty()) {
            throw new BaseException(NOT_FOUND_COVER_LETTER);
        }
        if (coverLetter.get().getState().equals(State.INACTIVE)) {
            throw new BaseException(BaseResponseStatus.ALREADY_DELETED_COVER_LETTER);
        }
        return coverLetter.get();
    }

    /**
     * 내가 등록했지만 아직 완성되지 않은 자소서 목록 조회
     *
     * @param pageable
     * @return
     */
    public List<GetCoverLettersRes> retrieveMyWritingCoverLetters(Pageable pageable) throws BaseException {
        Long userInfoId = jwtService.getUserInfo().getUserId();
        Page<CoverLetter> myCoverLetters = coverLetterRepository
                .findMyCoverLettersNotCompleted(pageable, userInfoId, State.ACTIVE, CoverLetterType.WRITING);
        return getMyCoverLettersResponses(myCoverLetters);
    }

    /**
     * 내가 완성한 자소서 목록 조회
     *
     * @param pageable
     * @return
     */
    public List<GetCoverLettersRes> retrieveMyCompletingCoverLetters(Pageable pageable) throws BaseException {
        Long userInfoId = jwtService.getUserInfo().getUserId();
        Page<CoverLetter> completingCoverLetters = coverLetterRepository
                .findMyCoverLetters(pageable, userInfoId, State.ACTIVE, CoverLetterType.COMPLETING);
        return getMyCoverLettersResponses(completingCoverLetters);
    }


    private List<GetCoverLettersRes> getMyCoverLettersResponses(Page<CoverLetter> coverLetterPage) {
        return coverLetterPage.stream()
                .map(coverLetter -> {
                    GetCoverLettersRes getCoverLettersRes = coverLetter.toGetCoverLetterRes();
                    setUserProfileToRes(coverLetter, getCoverLettersRes);
                    getCoverLettersRes.setIsSympathy(null);
                    getCoverLettersRes.setIsMine(null);
                    getCoverLettersRes.setSympathiesCount(null);
                    setCompletedCoverLetterContent(coverLetter, getCoverLettersRes);
                    return getCoverLettersRes;
                })
                .collect(toList());
    }

    private void setCompletedCoverLetterContent(CoverLetter coverLetter, GetCoverLettersRes getCoverLettersRes) {
        if (coverLetter.getType().equals(CoverLetterType.COMPLETING)) {
            Long originalCoverLetterId = coverLetter.getOriginalCoverLetterId();
            Optional<CoverLetter> optionalCoverLetter = coverLetterRepository.findById(originalCoverLetterId);
            optionalCoverLetter.ifPresent(originalCoverLetter ->
                    getCoverLettersRes.setCoverLetterContent(originalCoverLetter.getContent()));
            getCoverLettersRes.setCompletedCoverLetterContent(coverLetter.getContent());
        }
    }

    /**
     * 내가 공감한 자소서 목록 조회
     *
     * @param
     * @param
     * @return
     */
    public GetCoverLettersForLimitScrollRes retrieveMySympathizeCoverLetters(Pageable pageable) throws BaseException {
        Long userInfoId = jwtService.getUserInfo().getUserId();
        Page<CoverLetter> mySympathizeCoverLetters = getSympathizeCoverLetters(pageable, userInfoId);
        List<GetCoverLettersRes> coverLetters = getCoverLettersResponses(mySympathizeCoverLetters);
        return new GetCoverLettersForLimitScrollRes(coverLetters, mySympathizeCoverLetters.getTotalElements(), mySympathizeCoverLetters.hasNext());
    }

    private Page<CoverLetter> getSympathizeCoverLetters(Pageable pageable, Long userInfoId) {
        return coverLetterRepository
                .findMySympathizeCoverLetters(pageable, userInfoId, State.ACTIVE, CoverLetterType.WRITING);
    }

    /**
     * 유저가 오늘 작성한 자소서 개수 조회
     *
     * @return
     */
    public Long retrieveTodayWritingCoverLetterCount() throws BaseException {
        Long userInfoId = jwtService.getUserInfo().getUserId();
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        LocalDateTime startOfTomorrow = startOfToday.plusDays(ONE);
        return coverLetterRepository
                .getTodayWritingCoverLetterCount(userInfoId, startOfToday, startOfTomorrow, State.ACTIVE);
    }

    public GetCoverLetterToCompleteRes retrieveCoverLetterToComplete(Long coverLetterId) throws BaseException {
        CoverLetter originalCoverLetter = getCoverLetterById(coverLetterId);
        String originalCoverLetterCategoryName = originalCoverLetter.getCoverLetterCategory().getName();
        String originalCoverLetterContent = originalCoverLetter.getContent();
        Comment adoptedComment = originalCoverLetter.getAdoptedComment();
        String adoptedCommentContent = adoptedComment.getContent();
        return new GetCoverLetterToCompleteRes(coverLetterId, originalCoverLetterCategoryName,
                originalCoverLetterContent, adoptedCommentContent);
    }

    /**
     * 내가 쓴 자소서 개수 구하기
     * @param userInfo
     * @return
     */
    public Long getCoverLetterByUser(UserInfo userInfo) {
        return coverLetterRepository.countByUserInfoAndState(userInfo,State.ACTIVE);
    }

//    public Long getCompleteCoverLetterByUser(UserInfo userInfo) {
//        return coverLetterRepository.countByUserInfoAndStateAndType(userInfo,State.ACTIVE,CoverLetterType.COMPLETING);
//    }
}
