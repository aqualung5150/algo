package com.seungjoon.algo.user.dto;

import com.seungjoon.algo.user.domain.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponse {

    Long id;
    String email;
    String username;

    public UserResponse(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.username = user.getUsername();
    }
}
