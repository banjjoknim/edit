package com.app.edit.domain.advertisement;

import com.app.edit.config.BaseEntity;
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
@Table(name = "advertisement")
public class Advertisement extends BaseEntity {

    /*
     * 광고 ID
     **/
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    /*
     * 광고 이미지
     **/
    @Column(name = "image", nullable = false, updatable = false, columnDefinition = "TEXT")
    private String image;

    /*
     * 광고 디렉토리 ID
     **/
    @Column(name = "directory_id", nullable = false, updatable = false)
    private Long directoryId;

    /*
     * 광고 삭제 여부
     * default - ACTIVE
     **/
    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false, columnDefinition = "varchar(10) default 'ACTIVE'")
    private State state;

    @Builder
    public Advertisement(String image, Long directoryId, State state) {
        this.image = image;
        this.directoryId = directoryId;
        this.state = state;
    }
}
