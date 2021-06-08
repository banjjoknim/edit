package com.app.edit.provider;

import com.app.edit.config.BaseException;
import com.app.edit.config.BaseResponseStatus;
import com.app.edit.domain.coverletterdeclaration.CoverLetterDeclaration;
import com.app.edit.domain.coverletterdeclaration.CoverLetterDeclarationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
@Service
public class CoverLetterDeclarationProvider {

    private final CoverLetterDeclarationRepository coverLetterDeclarationRepository;

    @Autowired
    public CoverLetterDeclarationProvider(CoverLetterDeclarationRepository coverLetterDeclarationRepository) {
        this.coverLetterDeclarationRepository = coverLetterDeclarationRepository;
    }

    public CoverLetterDeclaration getCoverLetterDeclarationById(Long coverLetterDeclarationId) throws BaseException {
        Optional<CoverLetterDeclaration> coverLetterDeclaration = coverLetterDeclarationRepository
                .findById(coverLetterDeclarationId);
        if (coverLetterDeclaration.isEmpty()) {
            throw new BaseException(BaseResponseStatus.NOT_FOUND_COVER_LETTER_DECLARATION);
        }
        return coverLetterDeclaration.get();
    }
}
