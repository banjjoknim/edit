package com.app.edit.provider;

import com.app.edit.domain.coverletter.CoverLetter;
import com.app.edit.domain.sympathy.Sympathy;
import com.app.edit.domain.sympathy.SympathyId;
import com.app.edit.domain.sympathy.SympathyRepository;
import com.app.edit.enums.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
@Service
public class SympathyProvider {

    private final SympathyRepository sympathyRepository;

    @Autowired
    public SympathyProvider(SympathyRepository sympathyRepository) {
        this.sympathyRepository = sympathyRepository;
    }

    public Long getSympathiesCount(CoverLetter coverLetter) {
        return sympathyRepository.countSympathiesByCoverLetter(coverLetter);
    }

    public boolean getIsSympathy(Long coverLetterId, Long userInfoId) {
        SympathyId sympathyId = new SympathyId(coverLetterId, userInfoId);
        Optional<Sympathy> sympathy = sympathyRepository.findById(sympathyId);
        if (sympathy.isPresent()) {
            return sympathy.get().getState().equals(State.ACTIVE);
        }
        return false;
    }
}
