package com.seungjoon.algo.subject.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Tag {

    @Id /* 서버 시작 시 직접 할당(초기화) */
    @Column(name = "tag_id")
    private Long id;

    private String name;
}
