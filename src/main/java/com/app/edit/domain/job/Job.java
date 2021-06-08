package com.app.edit.domain.job;

import com.app.edit.config.BaseEntity;
import com.app.edit.domain.user.UserInfo;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.databind.ser.Serializers;
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
@Table(name = "job")
public class Job extends BaseEntity {

    /**
     * 직군 ID
     */
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 직군 이름
     */
    @Column(name = "name", columnDefinition = "varchar(20) not null")
    private String name;

    @OneToMany(mappedBy = "job",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private List<UserInfo> userInfoList;
}
