package com.app.edit.config;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseEntity {
//    @Getter
//    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", insertable = false, updatable = false)
//    @Temporal(TIMESTAMP)
    @CreationTimestamp
    @Column(name = "createdAt", nullable = false, updatable = false, columnDefinition = "timestamp default current_timestamp")
    private LocalDateTime createdAt;

//    @Getter
//    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP", insertable = false, updatable = false)
//    @Temporal(TIMESTAMP)
    @UpdateTimestamp
    @Column(name = "updatedAt", nullable = false, columnDefinition = "timestamp default current_timestamp on update current_timestamp")
    private LocalDateTime updatedAt;

//    @PrePersist
//    void prePersist() {
//        this.createdAt = this.updatedAt = new Date();
//    }
//
//    @PreUpdate
//    void preUpdate() {
//        this.updatedAt = new Date();
//    }
}