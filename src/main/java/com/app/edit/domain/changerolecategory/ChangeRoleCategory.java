package com.app.edit.domain.changerolecategory;

import com.app.edit.config.BaseEntity;
import com.app.edit.domain.certificationRequest.CertificationRequest;
import com.app.edit.domain.changerolereqeust.ChangeRoleRequest;
import com.app.edit.domain.job.Job;
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
@Table(name = "change_role_category")
public class ChangeRoleCategory extends BaseEntity {

    /**
     * 신청 사유 카테고리 번호
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    /**
     * 신청 사유 카테고리 이름
     */
    @Column(name = "name", nullable = false, length = 45)
    private String name;

    @OneToMany(mappedBy = "changeRoleCategory",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private List<ChangeRoleRequest> changeRoleRequestList;
}
