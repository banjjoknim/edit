package com.app.edit.domain.appreciate;

import com.app.edit.config.BaseEntity;
import com.app.edit.domain.comment.Comment;
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
@Table(name = "appreciate")
public class Appreciate extends BaseEntity {

    /*
     * 감사 ID
     * 복합키 -> commentId + userInfoId
     **/
    @EmbeddedId
    private AppreciateId appreciateId;

    @MapsId(value = "commentId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commentId", nullable = false)
    private Comment comment;

    @MapsId(value = "userInfoId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userInfoId", nullable = false)
    private UserInfo userInfo;

    /*
     * 감사 여부
     * default - ACTIVE
     **/
    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false, columnDefinition = "varchar(10) default 'ACTIVE'")
    private State state;

    @Builder
    public Appreciate(AppreciateId appreciateId, Comment comment, UserInfo userInfo, State state) {
        this.appreciateId = appreciateId;
        this.comment = comment;
        this.userInfo = userInfo;
        this.state = state;
    }
}
