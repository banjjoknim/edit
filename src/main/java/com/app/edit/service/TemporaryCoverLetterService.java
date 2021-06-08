package com.app.edit.service;

import com.app.edit.config.BaseException;
import com.app.edit.config.BaseResponseStatus;
import com.app.edit.domain.coverletter.CoverLetter;
import com.app.edit.domain.coverlettercategory.CoverLetterCategory;
import com.app.edit.domain.temporarycoverletter.TemporaryCoverLetter;
import com.app.edit.domain.temporarycoverletter.TemporaryCoverLetterRepository;
import com.app.edit.domain.user.UserInfo;
import com.app.edit.enums.CoverLetterType;
import com.app.edit.enums.State;
import com.app.edit.enums.UserRole;
import com.app.edit.provider.CoverLetterCategoryProvider;
import com.app.edit.provider.CoverLetterProvider;
import com.app.edit.provider.TemporaryCoverLetterProvider;
import com.app.edit.provider.UserInfoProvider;
import com.app.edit.request.temporarycoverletter.*;
import com.app.edit.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.app.edit.config.BaseResponseStatus.*;
import static com.app.edit.config.Constant.DEFAULT_ORIGINAL_COVER_LETTER_ID;

@Transactional
@Service
public class TemporaryCoverLetterService {

    private final TemporaryCoverLetterRepository temporaryCoverLetterRepository;
    private final TemporaryCoverLetterProvider temporaryCoverLetterProvider;
    private final CoverLetterCategoryProvider coverLetterCategoryProvider;
    private final CoverLetterProvider coverLetterProvider;
    private final CoverLetterService coverLetterService;
    private final UserInfoProvider userInfoProvider;
    private final JwtService jwtService;

    @Autowired
    public TemporaryCoverLetterService(TemporaryCoverLetterRepository temporaryCoverLetterRepository,
                                       TemporaryCoverLetterProvider temporaryCoverLetterProvider,
                                       CoverLetterCategoryProvider coverLetterCategoryProvider,
                                       CoverLetterProvider coverLetterProvider, CoverLetterService coverLetterService,
                                       UserInfoProvider userInfoProvider, JwtService jwtService) {
        this.temporaryCoverLetterRepository = temporaryCoverLetterRepository;
        this.temporaryCoverLetterProvider = temporaryCoverLetterProvider;
        this.coverLetterCategoryProvider = coverLetterCategoryProvider;
        this.coverLetterProvider = coverLetterProvider;
        this.coverLetterService = coverLetterService;
        this.userInfoProvider = userInfoProvider;
        this.jwtService = jwtService;
    }

    public Long createWritingTemporaryCoverLetter(PostWritingTemporaryCoverLetterReq request) throws BaseException {
        Long userInfoId = jwtService.getUserInfo().getUserId();
        Long coverLetterCategoryId = request.getCoverLetterCategoryId();
        String content = request.getCoverLetterContent();
        UserInfo userInfo = userInfoProvider.getUserInfoById(userInfoId);
        validateUserIsMentee(userInfo);
        CoverLetterCategory coverLetterCategory = coverLetterCategoryProvider
                .getCoverLetterCategoryById(coverLetterCategoryId);
        TemporaryCoverLetter requestedWritingTemporaryCoverLetter = TemporaryCoverLetter.builder()
                .coverLetterCategory(coverLetterCategory)
                .originalCoverLetterId(DEFAULT_ORIGINAL_COVER_LETTER_ID)
                .content(content)
                .state(State.ACTIVE)
                .type(CoverLetterType.WRITING)
                .build();
        userInfo.addTemporaryCoverLetter(requestedWritingTemporaryCoverLetter);
        TemporaryCoverLetter savedTemporaryCoverLetter = temporaryCoverLetterRepository
                .save(requestedWritingTemporaryCoverLetter);
        return savedTemporaryCoverLetter.getId();
    }

    public Long createCompletingTemporaryCoverLetter(PostCompletingTemporaryCoverLetterReq request) throws BaseException {
        Long userInfoId = jwtService.getUserInfo().getUserId();
        Long originalCoverLetterId = request.getOriginalCoverLetterId();
        String content = request.getCoverLetterContent();
        CoverLetter originalCoverLetter = coverLetterProvider.getCoverLetterById(originalCoverLetterId);
        CoverLetterCategory originalCoverLetterCategory = originalCoverLetter.getCoverLetterCategory();
        UserInfo userInfo = userInfoProvider.getUserInfoById(userInfoId);
        validateUserIsMentee(userInfo);
        TemporaryCoverLetter requestedCompletingTemporaryCoverLetter = TemporaryCoverLetter.builder()
                .coverLetterCategory(originalCoverLetterCategory)
                .originalCoverLetterId(originalCoverLetterId)
                .content(content)
                .state(State.ACTIVE)
                .type(CoverLetterType.COMPLETING)
                .build();
        userInfo.addTemporaryCoverLetter(requestedCompletingTemporaryCoverLetter);
        TemporaryCoverLetter savedTemporaryCoverLetter = temporaryCoverLetterRepository.save(requestedCompletingTemporaryCoverLetter);
        return savedTemporaryCoverLetter.getId();
    }

    public Long updateWritingTemporaryCoverLetterById(Long temporaryCoverLetterId, PatchWritingTemporaryCoverLetterReq request) throws BaseException {
        Long userInfoId = jwtService.getUserInfo().getUserId();
        UserInfo userInfo = userInfoProvider.getUserInfoById(userInfoId);
        validateUserIsMentee(userInfo);
        Long coverLetterCategoryId = request.getCoverLetterCategoryId();
        String coverLetterContent = request.getCoverLetterContent();
        CoverLetterCategory coverLetterCategory = coverLetterCategoryProvider
                .getCoverLetterCategoryById(coverLetterCategoryId);
        TemporaryCoverLetter temporaryCoverLetter = temporaryCoverLetterProvider
                .getTemporaryCoverLetterById(temporaryCoverLetterId);
        validateUser(userInfo, temporaryCoverLetter);
        temporaryCoverLetter.setCoverLetterCategory(coverLetterCategory);
        temporaryCoverLetter.setContent(coverLetterContent);
        temporaryCoverLetterRepository.save(temporaryCoverLetter);
        return temporaryCoverLetterId;
    }

    public Long updateCompletingTemporaryCoverLetterById(Long temporaryCoverLetterId, PatchCompletingTemporaryCoverLetterReq request) throws BaseException {
        Long userInfoId = jwtService.getUserInfo().getUserId();
        UserInfo userInfo = userInfoProvider.getUserInfoById(userInfoId);
        validateUserIsMentee(userInfo);
        String coverLetterContent = request.getCoverLetterContent();
        TemporaryCoverLetter temporaryCoverLetter = temporaryCoverLetterProvider
                .getTemporaryCoverLetterById(temporaryCoverLetterId);
        validateUser(userInfo, temporaryCoverLetter);
        temporaryCoverLetter.setContent(coverLetterContent);
        temporaryCoverLetterRepository.save(temporaryCoverLetter);
        return temporaryCoverLetterId;
    }

    private void validateUser(UserInfo userInfo, TemporaryCoverLetter temporaryCoverLetter) throws BaseException {
        if (!temporaryCoverLetter.getUserInfo().equals(userInfo)) {
            throw new BaseException(BaseResponseStatus.DO_NOT_HAVE_PERMISSION);
        }
    }

    private void validateUserIsMentee(UserInfo userInfo) throws BaseException {
        if (!userInfo.getUserRole().equals(UserRole.MENTEE)) {
            throw new BaseException(USER_ROLE_IS_NOT_MENTEE);
        }
    }

    public Long createWritingCoverLetterFromTemporary(PostWritingCoverLetterFromTemporaryReq request) throws BaseException {
        coverLetterService.validateTodayCoverLetterCount();
        Long userId = jwtService.getUserInfo().getUserId();
        UserInfo userInfo = userInfoProvider.getUserInfoById(userId);
        validateUserIsMentee(userInfo);
        Long temporaryCoverLetterId = request.getTemporaryCoverLetterId();
        Long coverLetterCategoryId = request.getCoverLetterCategoryId();
        String coverLetterContent = request.getCoverLetterContent();
        TemporaryCoverLetter temporaryCoverLetter = temporaryCoverLetterProvider.getTemporaryCoverLetterById(temporaryCoverLetterId);
        if (!temporaryCoverLetter.getType().equals(CoverLetterType.WRITING)) {
            throw new BaseException(FOUND_COVER_LETTER_TYPE_IS_NOT_WRITING);
        }
        validateUser(userInfo, temporaryCoverLetter);
        CoverLetterCategory coverLetterCategory = coverLetterCategoryProvider.getCoverLetterCategoryById(coverLetterCategoryId);
        CoverLetter coverLetter = CoverLetter.buildWritingCoverLetter(coverLetterCategory, coverLetterContent);
        userInfo.addCoverLetter(coverLetter);
        temporaryCoverLetter.deactivate();
        return coverLetterService.saveCoverLetter(coverLetter).getId();
    }

    public Long createCompletingCoverLetterFromTemporary(PostCompletingCoverLetterFromTemporaryReq request) throws BaseException {
        Long userId = jwtService.getUserInfo().getUserId();
        UserInfo userInfo = userInfoProvider.getUserInfoById(userId);
        validateUserIsMentee(userInfo);
        Long temporaryCoverLetterId = request.getTemporaryCoverLetterId();
        String coverLetterContent = request.getCoverLetterContent();
        TemporaryCoverLetter temporaryCoverLetter = temporaryCoverLetterProvider.getTemporaryCoverLetterById(temporaryCoverLetterId);
        if (!temporaryCoverLetter.getType().equals(CoverLetterType.COMPLETING)) {
            throw new BaseException(FOUND_COVER_LETTER_TYPE_IS_NOT_COMPLETING);
        }
        validateUser(userInfo, temporaryCoverLetter);
        CoverLetterCategory originalCoverLetterCategory = temporaryCoverLetter.getCoverLetterCategory();
        Long originalCoverLetterId = temporaryCoverLetter.getOriginalCoverLetterId();
        CoverLetter coverLetter = CoverLetter.buildCompletingCoverLetter(originalCoverLetterCategory, originalCoverLetterId, coverLetterContent);
        userInfo.addCoverLetter(coverLetter);
        temporaryCoverLetter.deactivate();
        return coverLetterService.saveCoverLetter(coverLetter).getId();
    }
}
