package com.seungjoon.algo.auth;

import com.seungjoon.algo.user.domain.User;
import com.seungjoon.algo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("user not found [" + email + "]"));

        if (user.getPassword() == null) {
            throw new UsernameNotFoundException("user not found [" + email + "]");
        }

        PrincipalDto principal = PrincipalDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .name(null)
                .role(user.getRole().name())
                .build();

        return new PrincipalDetails(principal);
    }
}
