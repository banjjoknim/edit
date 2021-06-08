package com.app.edit.provider;

import com.app.edit.domain.appreciate.Appreciate;
import com.app.edit.domain.appreciate.AppreciateRepository;
import com.app.edit.domain.comment.Comment;
import com.app.edit.domain.user.UserInfo;
import com.app.edit.enums.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
@Service
public class AppreciateProvider {

    private final AppreciateRepository appreciateRepository;

    @Autowired
    public AppreciateProvider(AppreciateRepository appreciateRepository) {
        this.appreciateRepository = appreciateRepository;
    }

    public Optional<Appreciate> getAppreciateByComment(Long userInfoId, Comment comment) {
        return appreciateRepository.findByUserInfoAndCommentAndState(userInfoId, comment, State.ACTIVE);
    }
}
