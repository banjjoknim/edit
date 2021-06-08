package com.app.edit.domain.changerolereqeust;

import com.app.edit.domain.user.UserInfo;
import com.app.edit.enums.IsProcessing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChangeRoleRequestRepository extends JpaRepository<ChangeRoleRequest, Long> {

    /**
     * 유저 역할 변경 신청 조회 쿼리
     * @param userInfo
     * @param isProcessing
     * @return
     */
    Optional<ChangeRoleRequest> findByUserInfoAndIsProcessing(UserInfo userInfo, IsProcessing isProcessing);
}
