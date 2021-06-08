package com.app.edit.domain.commentdeclaration;

import com.app.edit.config.BaseEntity;
import com.app.edit.domain.comment.Comment;
import com.app.edit.domain.user.UserInfo;
import com.app.edit.enums.IsAdopted;
import com.app.edit.enums.IsProcessing;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Accessors(chain = true)
@NoArgsConstructor
@Data
@Entity
@Table(name = "comment_declaration")
public class CommentDeclaration extends BaseEntity {

    /*
     * 코멘트 신고 ID
     **/
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /*
     * 신고당한 코멘트 ID
     **/
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commentId", nullable = false, updatable = false)
    private Comment comment;

    /*
     * 신고한 유저 ID
     **/
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userInfoId", nullable = false, updatable = false)
    private UserInfo userInfo;

    /*
     * 신고 처리 여부
     * default - NO
     **/
    @Enumerated(EnumType.STRING)
    @Column(name = "isProcessing", nullable = false, columnDefinition = "varchar(3) default 'NO'")
    private IsProcessing isProcessing;

    @Builder
    public CommentDeclaration(Comment comment, UserInfo userInfo, IsProcessing isProcessing) {
        this.comment = comment;
        this.userInfo = userInfo;
        this.isProcessing = isProcessing;
    }
}
