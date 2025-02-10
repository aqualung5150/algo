package com.seungjoon.algo.global;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

//TODO: @EntityListeners
@Getter
@MappedSuperclass
public abstract class BaseEntity {

    @CreatedBy
    @Column(updatable = false)
    String createdBy;

    @LastModifiedBy
    String lastModifiedBy;

    @CreatedDate
    @Column(updatable = false)
    LocalDateTime createdDate;

    @LastModifiedDate
    LocalDateTime lastModifiedDate;
}
