package com.app.edit.service;

import com.app.edit.config.BaseException;
import com.app.edit.config.BaseResponseStatus;
import com.app.edit.domain.coverletter.CoverLetter;
import com.app.edit.domain.coverletterdeclaration.CoverLetterDeclaration;
import com.app.edit.domain.coverletterdeclaration.CoverLetterDeclarationRepository;
import com.app.edit.domain.user.UserInfo;
import com.app.edit.enums.IsProcessing;
import com.app.edit.enums.UserRole;
import com.app.edit.provider.CoverLetterDeclarationProvider;
import com.app.edit.provider.CoverLetterProvider;
import com.app.edit.provider.UserInfoProvider;
import com.app.edit.request.coverletter.PostCoverLetterDeclarationReq;
import com.app.edit.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class CoverLetterDeclarationService {

    private final CoverLetterDeclarationRepository coverLetterDeclarationRepository;
    private final CoverLetterProvider coverLetterProvider;
    private final UserInfoProvider userInfoProvider;
    private final CoverLetterDeclarationProvider coverLetterDeclarationProvider;
    private final JwtService jwtService;

    @Autowired
    public CoverLetterDeclarationService(CoverLetterDeclarationRepository coverLetterDeclarationRepository,
                                         CoverLetterProvider coverLetterProvider, UserInfoProvider userInfoProvider,
                                         CoverLetterDeclarationProvider coverLetterDeclarationProvider,
                                         JwtService jwtService) {
        this.coverLetterDeclarationRepository = coverLetterDeclarationRepository;
        this.coverLetterProvider = coverLetterProvider;
        this.userInfoProvider = userInfoProvider;
        this.coverLetterDeclarationProvider = coverLetterDeclarationProvider;
        this.jwtService = jwtService;
    }

    public Long createCoverLetterDeclaration(PostCoverLetterDeclarationReq request) throws BaseException {
        Long userInfoId = jwtService.getUserInfo().getUserId();
        Long coverLetterId = request.getCoverLetterId();
        UserInfo userInfo = userInfoProvider.getUserInfoById(userInfoId);
        CoverLetter coverLetter = coverLetterProvider.getCoverLetterById(coverLetterId);
        CoverLetterDeclaration coverLetterDeclaration = CoverLetterDeclaration.builder()
                .coverLetter(coverLetter)
                .isProcessing(IsProcessing.NO)
                .build();
        userInfo.addCoverLetterDeclaration(coverLetterDeclaration);
        coverLetterDeclarationRepository.save(coverLetterDeclaration);
        return coverLetterId;
    }

    public Long processCoverLetterDeclaration(Long coverLetterDeclarationId) throws BaseException {
        Long userInfoId = jwtService.getUserInfo().getUserId();
        UserInfo userInfo = userInfoProvider.getUserInfoById(userInfoId);
        CoverLetterDeclaration coverLetterDeclaration = coverLetterDeclarationProvider.getCoverLetterDeclarationById(coverLetterDeclarationId);
        if (!userInfo.getUserRole().equals(UserRole.ADMIN)) {
            throw new BaseException(BaseResponseStatus.DO_NOT_HAVE_PERMISSION);
        }
        if (coverLetterDeclaration.getIsProcessing().equals(IsProcessing.YES)) {
            throw new BaseException(BaseResponseStatus.ALREADY_PROCESSED_COVER_LETTER_DECLARATION);
        }
        coverLetterDeclaration.setIsProcessing(IsProcessing.YES);
        coverLetterDeclarationRepository.save(coverLetterDeclaration);
        return coverLetterDeclarationId;
    }
}
