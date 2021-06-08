package com.app.edit.domain.comment;

import com.app.edit.config.BaseEntity;
import com.app.edit.domain.appreciate.Appreciate;
import com.app.edit.domain.commentdeclaration.CommentDeclaration;
import com.app.edit.domain.coverletter.CoverLetter;
import com.app.edit.domain.user.UserInfo;
import com.app.edit.enums.IsAdopted;
import com.app.edit.enums.State;
import com.app.edit.response.comment.CommentInfo;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.List;

import static com.app.edit.config.Constant.*;

@Accessors(chain = true)
@NoArgsConstructor
@Data
@Entity
@Table(name = "comment")
public class Comment extends BaseEntity {

    /*
     * 코멘트 ID
     **/
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    /*
     * 코멘트 등록한 유저 ID
     **/
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userInfoId", nullable = false, updatable = false)
    private UserInfo userInfo;

    /*
     * 자소서 ID
     **/
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coverLetterId", nullable = false, updatable = false)
    private CoverLetter coverLetter;

    /*
     * 문장에 대한 전체 평가
     * default - NONE
     **/
    @Column(name = "sentence_evaluation", nullable = false, columnDefinition = "varchar(10) default 'NONE'")
    private String sentenceEvaluation;

    /*
     * 구체성과 논리성
     * default - NONE
     **/
    @Column(name = "concreteness_logic", nullable = false, columnDefinition = "varchar(10) default 'NONE'")
    private String concretenessLogic;

    /*
     * 성실성
     * default - NONE
     **/
    @Column(name = "sincerity", nullable = false, columnDefinition = "varchar(10) default 'NONE'")
    private String sincerity;

    /*
     * 활동성
     * default - NONE
     **/
    @Column(name = "activity", nullable = false, columnDefinition = "varchar(10) default 'NONE'")
    private String activity;

    /*
     * 코멘트 내용
     **/
    @Column(name = "content", nullable = false, length = 300)
    private String content;

    /*
     * 코멘트 채택 여부
     * default - NO
     **/
    @Enumerated(EnumType.STRING)
    @Column(name = "isAdopted", nullable = false, columnDefinition = "varchar(3) default 'NO'")
    private IsAdopted isAdopted;

    /*
     * 삭제 여부
     * default - ACTIVE
     **/
    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false, columnDefinition = "varchar(10) default 'ACTIVE'")
    private State state;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL)
    private List<CommentDeclaration> commentDeclarations;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL)
    private List<Appreciate> appreciates;

    public void addCommentDeclaration(CommentDeclaration commentDeclaration) {
        this.commentDeclarations.add(commentDeclaration);
        commentDeclaration.setComment(this);
    }

    public void addAppreciate(Appreciate appreciate) {
        this.appreciates.add(appreciate);
        appreciate.setComment(this);
    }

    @Builder
    public Comment(UserInfo userInfo, CoverLetter coverLetter, String sentenceEvaluation, String concretenessLogic,
                   String sincerity, String activity, String content, IsAdopted isAdopted, State state,
                   List<CommentDeclaration> commentDeclarations, List<Appreciate> appreciates) {
        this.userInfo = userInfo;
        this.coverLetter = coverLetter;
        this.sentenceEvaluation = sentenceEvaluation;
        this.concretenessLogic = concretenessLogic;
        this.sincerity = sincerity;
        this.activity = activity;
        this.content = content;
        this.isAdopted = isAdopted;
        this.state = state;
        this.commentDeclarations = commentDeclarations;
        this.appreciates = appreciates;
    }

    public CommentInfo toCommentInfo() {
        Long commentId = this.id;
        String userProfile = DEFAULT_USER_PROFILE;
        String nickName = this.userInfo.getNickName();
        String jobName = this.userInfo.getJob().getName();
        String sentenceEvaluation = this.sentenceEvaluation;
        String concretenessLogic = this.concretenessLogic;
        String sincerity = this.sincerity;
        String activity = this.activity;
        String commentContent = this.content;
        IsAdopted isAdopted = this.isAdopted;
        boolean isMine = DEFAULT_IS_MINE;
        boolean isAppreciated = DEFAULT_APPRECIATED;
        return new CommentInfo(commentId, userProfile, nickName, jobName, sentenceEvaluation, concretenessLogic,
                sincerity, activity, commentContent, isAdopted, isMine, isAppreciated);
    }
}
