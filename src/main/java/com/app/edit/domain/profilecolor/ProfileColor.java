package com.app.edit.domain.profilecolor;

import com.app.edit.config.BaseEntity;
import com.app.edit.domain.certificationRequest.CertificationRequest;
import com.app.edit.domain.userprofile.UserProfile;
import com.app.edit.enums.State;
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
@Table(name = "profile_color")
public class ProfileColor extends BaseEntity {

    /**
     * 프로필 캐릭터 색상 등록 번호
     */
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 프로필 캐릭터 색상 이름
     */
    @Column(name = "name", nullable = false, length = 20)
    private String name;

    /**
     * 프로필 색깔 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "state", columnDefinition = "varchar(10) default 'ACTIVE'")
    private State state;

    @OneToMany(mappedBy = "profileColor",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private List<UserProfile> userProfileList;
}
