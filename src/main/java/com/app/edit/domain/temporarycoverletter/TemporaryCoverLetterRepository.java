package com.app.edit.domain.temporarycoverletter;

import com.app.edit.enums.CoverLetterType;
import com.app.edit.enums.State;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TemporaryCoverLetterRepository extends JpaRepository<TemporaryCoverLetter, Long> {

    /**
     * 임시 저장한 자소서 조회 쿼리
     * @param pageable
     * @param userInfoId
     * @param state
     * @param coverLetterType
     * @return
     */
    @Query(value = "select tcl from TemporaryCoverLetter tcl where tcl.userInfo.id = :userInfoId and tcl.state = :state and tcl.type = :coverLetterType")
    Page<TemporaryCoverLetter> findTemporaryCoverLetters(Pageable pageable, @Param("userInfoId") Long userInfoId,
                                                         @Param("state") State state,
                                                         @Param("coverLetterType") CoverLetterType coverLetterType);
}
