package com.app.edit.domain.temporarycomment;

import com.app.edit.config.BaseEntity;
import com.app.edit.domain.coverletter.CoverLetter;
import com.app.edit.domain.user.UserInfo;
import com.app.edit.enums.State;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Accessors(chain = true)
@NoArgsConstructor
@Data
@Entity
@Table(name = "temporary_comment")
public class TemporaryComment extends BaseEntity {

    /*
     * 임시 저장된 코멘트 ID
     **/
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    /*
     * 코멘트를 임시 저장한 유저 ID
     **/
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userInfoId", nullable = false, updatable = false)
    private UserInfo userInfo;

    /*
     * 임시 저장된 코멘트를 달려고 했던 자소서 ID
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
    @Column(name = "content", nullable = false, length = 90)
    private String content;

    /*
     * 삭제 여부
     * default - ACTIVE
     **/
    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false, columnDefinition = "varchar(10) default 'ACTIVE'")
    private State state;

    @Builder
    public TemporaryComment(UserInfo userInfo, CoverLetter coverLetter, String sentenceEvaluation,
                            String concretenessLogic, String sincerity, String activity, String content, State state) {
        this.userInfo = userInfo;
        this.coverLetter = coverLetter;
        this.sentenceEvaluation = sentenceEvaluation;
        this.concretenessLogic = concretenessLogic;
        this.sincerity = sincerity;
        this.activity = activity;
        this.content = content;
        this.state = state;
    }
}
