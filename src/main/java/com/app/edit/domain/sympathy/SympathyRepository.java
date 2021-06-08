package com.app.edit.domain.sympathy;

import com.app.edit.config.PageRequest;
import com.app.edit.domain.coverletter.CoverLetter;
import com.app.edit.enums.State;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SympathyRepository extends JpaRepository<Sympathy, SympathyId> {

    Long countSympathiesByCoverLetter(CoverLetter coverLetter);
}
