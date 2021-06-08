package com.app.edit.provider;

import com.app.edit.config.BaseException;
import com.app.edit.domain.commentdeclaration.CommentDeclaration;
import com.app.edit.domain.commentdeclaration.CommentDeclarationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.app.edit.config.BaseResponseStatus.NOT_FOUND_COMMENT_DECLARATION;

@Transactional(readOnly = true)
@Service
public class CommentDeclarationProvider {

    private final CommentDeclarationRepository commentDeclarationRepository;

    @Autowired
    public CommentDeclarationProvider(CommentDeclarationRepository commentDeclarationRepository) {
        this.commentDeclarationRepository = commentDeclarationRepository;
    }

    public CommentDeclaration getCommentDeclarationById(Long commentDeclarationId) throws BaseException {
        Optional<CommentDeclaration> commentDeclaration = commentDeclarationRepository.findById(commentDeclarationId);
        if (commentDeclaration.isEmpty()) {
            throw new BaseException(NOT_FOUND_COMMENT_DECLARATION);
        }
        return commentDeclaration.get();
    }
}
