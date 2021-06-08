package com.app.edit.domain.changerolereqeust;

import com.app.edit.config.BaseEntity;
import com.app.edit.domain.changerolecategory.ChangeRoleCategory;
import com.app.edit.domain.user.UserInfo;
import com.app.edit.enums.IsProcessing;
import com.app.edit.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.List;

@Accessors(chain = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Data
@Entity
@Table(name = "change_role_reqeust")
public class ChangeRoleRequest extends BaseEntity {

    /**
     * 역할 변경 요청 정보 번호
     */
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 신청 사유 내용
     */
    @Column(name = "content",nullable = false, length = 100)
    private String content;

    /**
     * 이전 역할
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "role",length = 6)
    private UserRole previousRole;

    /**
     * 변경 요청 처리 여부
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "isProcessing",columnDefinition = "varchar(3) default 'NO'")
    private IsProcessing isProcessing;

    @ManyToOne
    @JoinColumn(name = "userInfoId",referencedColumnName = "id")
    private UserInfo userInfo;

    @ManyToOne
    @JoinColumn(name = "roleCategoryId")
    private ChangeRoleCategory changeRoleCategory;

    public void process() {
        isProcessing = IsProcessing.YES;
    }
}
