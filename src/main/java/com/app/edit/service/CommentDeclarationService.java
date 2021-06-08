package com.app.edit.service;

import com.app.edit.config.BaseException;
import com.app.edit.config.BaseResponseStatus;
import com.app.edit.domain.comment.Comment;
import com.app.edit.domain.commentdeclaration.CommentDeclaration;
import com.app.edit.domain.commentdeclaration.CommentDeclarationRepository;
import com.app.edit.domain.user.UserInfo;
import com.app.edit.enums.IsProcessing;
import com.app.edit.enums.UserRole;
import com.app.edit.provider.CommentDeclarationProvider;
import com.app.edit.provider.CommentProvider;
import com.app.edit.provider.UserInfoProvider;
import com.app.edit.request.comment.PostCommentDeclarationReq;
import com.app.edit.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.app.edit.config.BaseResponseStatus.ALREADY_PROCESSED_COMMENT_DECLARATION;

@Transactional
@Service
public class CommentDeclarationService {

    private final CommentDeclarationRepository commentDeclarationRepository;
    private final UserInfoProvider userInfoProvider;
    private final CommentProvider commentProvider;
    private final CommentDeclarationProvider commentDeclarationProvider;
    private final JwtService jwtService;

    @Autowired
    public CommentDeclarationService(CommentDeclarationRepository commentDeclarationRepository,
                                     UserInfoProvider userInfoProvider, CommentProvider commentProvider,
                                     CommentDeclarationProvider commentDeclarationProvider, JwtService jwtService) {
        this.commentDeclarationRepository = commentDeclarationRepository;
        this.userInfoProvider = userInfoProvider;
        this.commentProvider = commentProvider;
        this.commentDeclarationProvider = commentDeclarationProvider;
        this.jwtService = jwtService;
    }

    public Long createCommentDeclaration(PostCommentDeclarationReq request) throws BaseException {
        Long userId = jwtService.getUserInfo().getUserId();
        Long commentId = request.getCommentId();
        UserInfo userInfo = userInfoProvider.getUserInfoById(userId);
        Comment comment = commentProvider.getCommentById(commentId);
        CommentDeclaration commentDeclaration = CommentDeclaration.builder()
                .comment(comment)
                .isProcessing(IsProcessing.NO)
                .build();
        userInfo.addCommentDeclaration(commentDeclaration);
        commentDeclarationRepository.save(commentDeclaration);
        return commentId;
    }

    public Long processCommentDeclaration(Long commentDeclarationId) throws BaseException {
        Long userInfoId = jwtService.getUserInfo().getUserId();
        UserInfo userInfo = userInfoProvider.getUserInfoById(userInfoId);
        CommentDeclaration commentDeclaration = commentDeclarationProvider.getCommentDeclarationById(commentDeclarationId);
        if (!userInfo.getUserRole().equals(UserRole.ADMIN)) {
            throw new BaseException(BaseResponseStatus.DO_NOT_HAVE_PERMISSION);
        }
        if (commentDeclaration.getIsProcessing().equals(IsProcessing.YES)) {
            throw new BaseException(ALREADY_PROCESSED_COMMENT_DECLARATION);
        }
        commentDeclaration.setIsProcessing(IsProcessing.YES);
        commentDeclarationRepository.save(commentDeclaration);
        return commentDeclarationId;
    }
}
