package com.app.edit.domain.appreciate;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@NoArgsConstructor
@Embeddable
public class AppreciateId implements Serializable {

    @Column(name = "commentId", nullable = false, updatable = false)
    private Long commentId;

    @Column(name = "userInfoId", nullable = false, updatable = false)
    private Long userInfoId;

    @Builder
    public AppreciateId(Long commentId, Long userInfoId) {
        this.commentId = commentId;
        this.userInfoId = userInfoId;
    }
}
