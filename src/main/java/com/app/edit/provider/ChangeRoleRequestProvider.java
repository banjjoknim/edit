package com.app.edit.provider;

import com.app.edit.domain.changerolereqeust.ChangeRoleRequest;
import com.app.edit.domain.changerolereqeust.ChangeRoleRequestRepository;
import com.app.edit.domain.user.UserInfo;
import com.app.edit.enums.IsProcessing;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ChangeRoleRequestProvider {

    private final ChangeRoleRequestRepository changeRoleRequestRepository;

    public Optional<ChangeRoleRequest> getChangeRoleRequestByUserInfo(UserInfo userInfo) {
        return changeRoleRequestRepository.findByUserInfoAndIsProcessing(userInfo, IsProcessing.NO);
    }
}
