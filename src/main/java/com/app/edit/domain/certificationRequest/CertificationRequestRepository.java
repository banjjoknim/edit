package com.app.edit.domain.certificationRequest;

import com.app.edit.domain.mentor.MentorInfo;
import com.app.edit.domain.user.UserInfo;
import com.app.edit.enums.IsProcessing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CertificationRequestRepository extends JpaRepository<CertificationRequest,Long> {


    Optional<CertificationRequest> findTop1ByIsProcessingAndUserInfoOrderByCreatedAtDesc(IsProcessing no, UserInfo userInfo);

}
