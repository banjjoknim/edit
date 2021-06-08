package com.app.edit.domain.coverletterdeclaration;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoverLetterDeclarationRepository extends JpaRepository<CoverLetterDeclaration, Long> {
}
