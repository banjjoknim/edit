package com.app.edit.provider;

import com.app.edit.config.BaseException;
import com.app.edit.domain.appreciate.Appreciate;
import com.app.edit.domain.comment.Comment;
import com.app.edit.domain.comment.CommentRepository;
import com.app.edit.domain.coverletter.CoverLetter;
import com.app.edit.domain.user.UserInfo;
import com.app.edit.enums.IsAdopted;
import com.app.edit.enums.State;
import com.app.edit.response.comment.*;
import com.app.edit.response.coverletter.GetCoverLettersByCommentRes;
import com.app.edit.response.coverletter.GetCoverLettersRes;
import com.app.edit.response.user.GetUserInfosRes;
import com.app.edit.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.app.edit.config.BaseResponseStatus.*;
import static com.app.edit.config.Constant.PROFILE_SEPARATOR;
import static java.util.stream.Collectors.toList;

@Transactional(readOnly = true)
@Service
public class CommentProvider {

    private final CommentRepository commentRepository;
    private final CoverLetterProvider coverLetterProvider;
    private final UserProvider userProvider;
    private final AppreciateProvider appreciateProvider;
    private final JwtService jwtService;

    @Autowired
    public CommentProvider(CommentRepository commentRepository, CoverLetterProvider coverLetterProvider,
                           UserProvider userProvider, AppreciateProvider appreciateProvider, JwtService jwtService) {
        this.commentRepository = commentRepository;
        this.coverLetterProvider = coverLetterProvider;
        this.userProvider = userProvider;
        this.appreciateProvider = appreciateProvider;
        this.jwtService = jwtService;
    }

    public GetCommentsRes retrieveCommentsByCoverLetterId(Pageable pageable, Long coverLetterId) throws BaseException {
        Long userInfoId = jwtService.getUserInfo().getUserId();
        CoverLetter coverLetter = coverLetterProvider.getCoverLetterById(coverLetterId);
        List<CommentInfo> commentInfos = commentRepository.findCommentsByCoverLetter(pageable, coverLetter, State.ACTIVE).stream()
                .map(comment -> {
                    CommentInfo commentInfo = comment.toCommentInfo();
                    setIsMineToCommentInfo(userInfoId, comment, commentInfo);
                    setIsAppreciatedToCommentInfo(userInfoId, comment, commentInfo);
                    setUserProfileToCommentInfo(comment, commentInfo);
                    return commentInfo;
                })
                .collect(toList());
        GetCoverLettersRes coverLetterInfo = coverLetter.toGetCoverLetterRes();
        if (coverLetter.getUserInfo().getId().equals(userInfoId)) {
            coverLetterInfo.setIsMine(true);
        }
        coverLetterInfo.setIsSympathy(null);
        coverLetterInfo.setSympathiesCount(null);
        return new GetCommentsRes(coverLetterInfo, commentInfos);
    }

    private void setIsAppreciatedToCommentInfo(Long userInfoId, Comment comment, CommentInfo commentInfo) {
        Optional<Appreciate> appreciate = appreciateProvider.getAppreciateByComment(userInfoId, comment);
        appreciate.ifPresent(selectedAppreciate -> commentInfo.setIsAppreciated(true));
    }

    private void setIsMineToCommentInfo(Long userInfoId, Comment comment, CommentInfo commentInfo) {
        if (comment.getUserInfo().getId().equals(userInfoId)) {
            commentInfo.setIsMine(true);
        }
    }

    private void setUserProfileToCommentInfo(Comment comment, CommentInfo commentInfo) {
        String profileColorName = comment.getUserInfo().getUserProfile().getProfileColor().getName();
        String profileEmotionName = comment.getUserInfo().getUserProfile().getProfileEmotion().getName();
        commentInfo.setUserProfile(profileColorName + PROFILE_SEPARATOR + profileEmotionName);
    }

    public Comment getCommentById(Long commentId) throws BaseException {
        Optional<Comment> comment = commentRepository.findById(commentId);
        if (comment.isEmpty()) {
            throw new BaseException(NOT_FOUND_COMMENT);
        }
        if (comment.get().getState().equals(State.INACTIVE)) {
            throw new BaseException(ALREADY_DELETED_COMMENT);
        }
        return comment.get();
    }

    public GetNotAdoptedCommentContentsRes getNotAdoptedCommentContentsById(Long coverLetterId, Pageable pageable) throws BaseException {
        CoverLetter coverLetter = coverLetterProvider.getCoverLetterById(coverLetterId);
        Page<Comment> notAdoptedComments = commentRepository
                .findNotAdoptedCommentsByCoverLetter(pageable, coverLetter, IsAdopted.NO, State.ACTIVE);
        List<String> notAdoptedCommentContents = notAdoptedComments.stream()
                .map(Comment::getContent)
                .collect(toList());
        return new GetNotAdoptedCommentContentsRes(notAdoptedCommentContents);
    }

    /**
     * 내가 작성한 코멘트 조회
     * @param pageable
     * @param userInfoId
     * @return
     */
    public List<GetMyCommentsRes> retrieveMyComments(Pageable pageable, Long userInfoId) throws BaseException{

        // 내가 작성한 코멘트 조회
        Page<Comment> commentList = commentRepository.findByUser(pageable, userInfoId, State.ACTIVE);

        if(commentList.getSize() == 0)
            throw new BaseException(NOT_FOUND_COMMENT);

        GetUserInfosRes getUserInfosRes = userProvider.retrieveSympathizeUser(userInfoId);

        return commentList.stream()
                .map(comment -> GetMyCommentsRes.builder()
                        .userInfo(getUserInfosRes)
                        .commentInfo(GetMyCommentRes.builder()
                                .commentId(comment.getId())
                                .activity(comment.getActivity())
                                .commentContent(comment.getContent())
                                .concretenessLogic(comment.getConcretenessLogic())
                                .sentenceEvaluation(comment.getSentenceEvaluation())
                                .sincerity(comment.getSincerity())
                                .build())
                        .build())
                .collect(toList());
    }

    /**
     * 내가 작성한 코멘트 문장 조회
     * @return
     */
    public GetMyCommentWithCoverLetterRes getMyCommentWithCoverLetter(Long commentId, Long userInfoId) throws BaseException {

        Comment comment = commentRepository.findByIdAndState(commentId,State.ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_FOUND_COMMENT));

        CoverLetter coverLetter = coverLetterProvider.getCoverLetterById(comment.getCoverLetter().getId());

        return GetMyCommentWithCoverLetterRes.builder()
                .commentRes(GetMyCommentsRes.builder()
                        .commentInfo(commentToGetMyCommentRes(comment))
                        .userInfo(userProvider.retrieveSympathizeUser(userInfoId))
                        .build())
                .coverLetterRes(GetCoverLettersByCommentRes.builder()
                        .userInfo(userProvider.retrieveSympathizeUser(coverLetter.getUserInfo().getId()))
                        .coverLetterId(coverLetter.getId())
                        .coverLetterContent(coverLetter.getContent())
                        .coverLetterCategoryName(coverLetter.getCoverLetterCategory().getName())
                        .build())
                .build();
    }


    public GetMyCommentRes commentToGetMyCommentRes(Comment comment){
        return GetMyCommentRes.builder()
                .commentId(comment.getId())
                .activity(comment.getActivity())
                .commentContent(comment.getContent())
                .concretenessLogic(comment.getConcretenessLogic())
                .sentenceEvaluation(comment.getSentenceEvaluation())
                .sincerity(comment.getSincerity())
                .build();
    }


    /**
     * 코멘트 등록할때 자소서 조회
     * @param userInfoId
     * @param coverLetterId
     * @return
     */
    public GetCoverLettersByCommentRes retrieveCommentWithCoverLetter(Long userInfoId, Long coverLetterId) throws BaseException {

        CoverLetter coverLetter = coverLetterProvider.getCoverLetterById(coverLetterId);


        return GetCoverLettersByCommentRes.builder()
                .userInfo(userProvider.retrieveSympathizeUser(userInfoId))
                .coverLetterId(coverLetterId)
                .coverLetterContent(coverLetter.getContent())
                .coverLetterCategoryName(null)
                .build();
    }

    /**
     * 내 코멘트 작성된 수
     */
    public Long retrieveMyCommentCount(Long userInfoId){
        return commentRepository.findByUserAndState(userInfoId,State.ACTIVE);
    }

//    /**
//     * 내 채택된 코멘트 수
//     * @return
//     */
//    public Long retrieveMyAdoptCommentCount(Long userInfoId) {
//        return commentRepository.findUserAndStateAndAdopt(userInfoId,State.ACTIVE,IsAdopted.YES);
//    }


}
