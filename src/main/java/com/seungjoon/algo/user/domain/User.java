package com.seungjoon.algo.user.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String username;

    private String password;

    private String imageUrl;

    String authType;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private UserState state;

    public User(String email, String username) {
        this.email = email;
        this.username = username;
    }

    @Builder
    public User(String email, String username, String password, String imageUrl, String authType, Role role, UserState state) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.imageUrl = imageUrl;
        this.authType = authType;
        this.role = role;
        this.state = state;

        if (imageUrl == null) {
            this.imageUrl = "default.jpg";
        }
    }

    public void changeImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public void changeUsername(String username) { this.username = username; }
    public void changeRole(Role role) { this.role = role; }
}
