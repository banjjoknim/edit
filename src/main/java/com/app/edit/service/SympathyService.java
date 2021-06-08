package com.app.edit.service;

import com.app.edit.config.BaseException;
import com.app.edit.domain.coverletter.CoverLetter;
import com.app.edit.domain.sympathy.Sympathy;
import com.app.edit.domain.sympathy.SympathyId;
import com.app.edit.domain.sympathy.SympathyRepository;
import com.app.edit.domain.user.UserInfo;
import com.app.edit.enums.State;
import com.app.edit.provider.CoverLetterProvider;
import com.app.edit.provider.UserInfoProvider;
import com.app.edit.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@Service
public class SympathyService {

    private final SympathyRepository sympathyRepository;
    private final UserInfoProvider userInfoProvider;
    private final CoverLetterProvider coverLetterProvider;
    private final JwtService jwtService;

    @Autowired
    public SympathyService(SympathyRepository sympathyRepository, UserInfoProvider userInfoProvider,
                           CoverLetterProvider coverLetterProvider, JwtService jwtService) {
        this.sympathyRepository = sympathyRepository;
        this.userInfoProvider = userInfoProvider;
        this.coverLetterProvider = coverLetterProvider;
        this.jwtService = jwtService;
    }

    public SympathyId createOrUpdateSympathy(Long coverLetterId) throws BaseException {
        Long userInfoId = jwtService.getUserInfo().getUserId();
        UserInfo userInfo = userInfoProvider.getUserInfoById(userInfoId);
        CoverLetter coverLetter = coverLetterProvider.getCoverLetterById(coverLetterId);
        SympathyId sympathyId = new SympathyId(coverLetterId, userInfoId);
        Optional<Sympathy> sympathy = sympathyRepository.findById(sympathyId);
        if (sympathy.isEmpty()) {
            return createSympathy(userInfo, coverLetter, sympathyId);
        }
        return updateSympathy(sympathy);
    }

    private SympathyId createSympathy(UserInfo userInfo, CoverLetter coverLetter, SympathyId sympathyId) {
        Sympathy sympathy = Sympathy.builder()
                .sympathyId(sympathyId)
                .userInfo(userInfo)
                .coverLetter(coverLetter)
                .state(State.ACTIVE)
                .build();
        Sympathy savedSympathy = sympathyRepository.save(sympathy);
        userInfo.addSympathy(savedSympathy);
        return savedSympathy.getSympathyId();
    }

    private SympathyId updateSympathy(Optional<Sympathy> sympathy) {
        Sympathy oldSympathy = sympathy.get();
        if (oldSympathy.getState().equals(State.ACTIVE)) {
            oldSympathy.setState(State.INACTIVE);
            sympathyRepository.save(oldSympathy);
            return oldSympathy.getSympathyId();
        }
        oldSympathy.setState(State.ACTIVE);
        sympathyRepository.save(oldSympathy);
        return oldSympathy.getSympathyId();
    }
}
