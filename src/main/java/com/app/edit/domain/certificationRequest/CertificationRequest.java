package com.app.edit.domain.certificationRequest;

import com.app.edit.config.BaseEntity;
import com.app.edit.domain.job.Job;
import com.app.edit.domain.user.UserInfo;
import com.app.edit.enums.AuthenticationCheck;
import com.app.edit.enums.IsProcessing;
import com.app.edit.enums.State;
import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Accessors(chain = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Data
@Entity
@Table(name = "certification_request")
public class CertificationRequest extends BaseEntity {

    /**
     * 멘토 등록 인증 번호
     */
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 클라이언트 측에서 인증 수단을 보내는 방법이 없어서 주석 처리
     */
//    /**
//     * 멘토 등록 인증 수단
//     */
//    @Column(name = "type",nullable = false, length = 15)
//    private String type;

    /**
     * 멘토 등록 인증 요청시 사용한 이미지
     */
    @Column(name = "imageUrl", columnDefinition = "TEXT")
    private String imageUrl;

    /**
     * 멘토 등록 인증 처리 여부
     */
    @Enumerated(value = EnumType.STRING)
    @Column(name = "isProcessing", columnDefinition = "varchar(3) default 'NO'")
    private IsProcessing isProcessing;

    /**
     * 멘토 등록 인증 요청한 회원 번호
     */
    @ManyToOne
    @JoinColumn(name = "userInfoId",nullable = false)
    private UserInfo userInfo;

    /**
     * default 값 정의
     */
    @PrePersist
    public void prePersist() {
        this.isProcessing =
                this.isProcessing == null ? IsProcessing.NO : this.isProcessing;
    }

}
