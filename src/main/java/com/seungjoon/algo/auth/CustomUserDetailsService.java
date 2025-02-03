package com.seungjoon.algo.auth;

import com.seungjoon.algo.member.domain.Member;
import com.seungjoon.algo.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("user not found [" + email + "]"));

        if (member.getPassword() == null) {
            throw new UsernameNotFoundException("user not found [" + email + "]");
        }

        PrincipalDto principal = PrincipalDto.builder()
                .id(member.getId())
                .username(member.getUsername())
                .password(member.getPassword())
                .name(null)
                .role(member.getRole().name())
                .build();

        return new PrincipalDetails(principal);
    }
}
