package com.app.edit.service;

import com.app.edit.domain.changerolereqeust.ChangeRoleRequest;
import com.app.edit.domain.changerolereqeust.ChangeRoleRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class ChangeRoleRequestService {

    private final ChangeRoleRequestRepository changeRoleRequestRepository;

    public void updateChangeRoleRequest(ChangeRoleRequest changeRoleRequest) {
        changeRoleRequestRepository.save(changeRoleRequest);
    }
}
