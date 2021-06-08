package com.app.edit.domain.mentor;

import com.app.edit.config.BaseEntity;
import com.app.edit.config.BaseException;
import com.app.edit.domain.user.UserInfo;
import com.app.edit.enums.State;
import lombok.*;
import lombok.experimental.Accessors;
import org.apache.catalina.User;

import javax.persistence.*;
import java.io.Serializable;

@Accessors(chain = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Data
@Entity
@Table(name = "mentor_info")
public class MentorInfo extends BaseEntity{

    /**
     * 멘토 ID
     */
    @Id
    @Column(name = "id",nullable = false,updatable = false)
    private Long id;

    @MapsId
    @OneToOne
    @JoinColumn(name = "userInfoId")
    private UserInfo userInfo;

    /**
     * 멘토 사진
     */
    @Column(name = "name", columnDefinition = "TEXT")
    private String name;

    /**
     * 멘토 저장 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "state", columnDefinition = "varchar(10) default 'ACTIVE'")
    private State state;




}
