package com.seungjoon.algo.recruit.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

public class ApplicantId implements Serializable {

    private Long recruitPost;
    private Long member;
}
