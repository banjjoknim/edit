package com.app.edit.domain.sympathy;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@NoArgsConstructor
@Embeddable
public class SympathyId implements Serializable {

    @Column(name = "coverLetterId", nullable = false, updatable = false)
    private Long coverLetterId;

    @Column(name = "userInfoId", nullable = false, updatable = false)
    private Long userInfoId;

    @Builder
    public SympathyId(Long coverLetterId, Long userInfoId) {
        this.coverLetterId = coverLetterId;
        this.userInfoId = userInfoId;
    }
}
