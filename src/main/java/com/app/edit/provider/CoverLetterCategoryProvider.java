package com.app.edit.provider;

import com.app.edit.config.BaseException;
import com.app.edit.domain.coverlettercategory.CoverLetterCategory;
import com.app.edit.domain.coverlettercategory.CoverLetterCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.app.edit.config.BaseResponseStatus.NOT_FOUND_COVER_LETTER_CATEGORY;

@Transactional(readOnly = true)
@Service
public class CoverLetterCategoryProvider {

    private final CoverLetterCategoryRepository coverLetterCategoryRepository;

    @Autowired
    public CoverLetterCategoryProvider(CoverLetterCategoryRepository coverLetterCategoryRepository) {
        this.coverLetterCategoryRepository = coverLetterCategoryRepository;
    }

    public CoverLetterCategory getCoverLetterCategoryById(Long coverLetterCategoryId) throws BaseException {
        Optional<CoverLetterCategory> coverLetterCategory = coverLetterCategoryRepository.findById(coverLetterCategoryId);
        if (coverLetterCategory.isEmpty()) {
            throw new BaseException(NOT_FOUND_COVER_LETTER_CATEGORY);
        }
        return coverLetterCategory.get();
    }
}
