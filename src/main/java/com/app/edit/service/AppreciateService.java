package com.app.edit.service;

import com.app.edit.config.BaseException;
import com.app.edit.domain.appreciate.Appreciate;
import com.app.edit.domain.appreciate.AppreciateId;
import com.app.edit.domain.appreciate.AppreciateRepository;
import com.app.edit.domain.comment.Comment;
import com.app.edit.domain.user.UserInfo;
import com.app.edit.enums.State;
import com.app.edit.provider.CommentProvider;
import com.app.edit.provider.UserInfoProvider;
import com.app.edit.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@Service
public class AppreciateService {

    private final AppreciateRepository appreciateRepository;
    private final CommentProvider commentProvider;
    private final UserInfoProvider userInfoProvider;
    private final JwtService jwtService;

    @Autowired
    public AppreciateService(AppreciateRepository appreciateRepository, CommentProvider commentProvider,
                             UserInfoProvider userInfoProvider, JwtService jwtService) {
        this.appreciateRepository = appreciateRepository;
        this.commentProvider = commentProvider;
        this.userInfoProvider = userInfoProvider;
        this.jwtService = jwtService;
    }

    public AppreciateId createOrUpdateAppreciate(Long commentId) throws BaseException {
        Long userInfoId = jwtService.getUserInfo().getUserId();
        UserInfo userInfo = userInfoProvider.getUserInfoById(userInfoId);
        Comment comment = commentProvider.getCommentById(commentId);
        AppreciateId appreciateId = new AppreciateId(commentId, userInfoId);
        Optional<Appreciate> appreciate = appreciateRepository.findById(appreciateId);
        if (appreciate.isEmpty()) {
            return createAppreciate(userInfo, comment, appreciateId);
        }
        return updateAppreciate(appreciate);
    }

    private AppreciateId createAppreciate(UserInfo userInfo, Comment comment, AppreciateId appreciateId) {
        Appreciate newAppreciate = Appreciate.builder()
                .appreciateId(appreciateId)
                .userInfo(userInfo)
                .comment(comment)
                .state(State.ACTIVE)
                .build();
        Appreciate savedAppreciate = appreciateRepository.save(newAppreciate);
        userInfo.addAppreciate(savedAppreciate);
        return savedAppreciate.getAppreciateId();
    }

    private AppreciateId updateAppreciate(Optional<Appreciate> appreciate) {
        Appreciate oldAppreciate = appreciate.get();
        if (oldAppreciate.getState().equals(State.ACTIVE)) {
            oldAppreciate.setState(State.INACTIVE);
            appreciateRepository.save(oldAppreciate);
            return oldAppreciate.getAppreciateId();
        }
        oldAppreciate.setState(State.ACTIVE);
        appreciateRepository.save(oldAppreciate);
        return oldAppreciate.getAppreciateId();
    }
}
