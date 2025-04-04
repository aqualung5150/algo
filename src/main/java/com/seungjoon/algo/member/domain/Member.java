package com.seungjoon.algo.member.domain;

import com.seungjoon.algo.global.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String username;

    private String password;

    private String imageUrl;

    private String authType;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private MemberState state;

    public Member(String email, String username) {
        this.email = email;
        this.username = username;
    }

    @Builder
    private Member(String email, String username, String password, String imageUrl, String authType, Role role, MemberState state) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.imageUrl = imageUrl;
        this.authType = authType;
        this.role = role;
        this.state = state;
    }

    public void changeImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public void changeUsername(String username) { this.username = username; }
    public void changeRole(Role role) { this.role = role; }

    public void updateMember(String username, String imageUrl) {
        this.username = username;
        this.imageUrl = imageUrl;
    }
}
