package com.app.edit.domain.coverlettercategory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoverLetterCategoryRepository extends JpaRepository<CoverLetterCategory, Long> {
}
