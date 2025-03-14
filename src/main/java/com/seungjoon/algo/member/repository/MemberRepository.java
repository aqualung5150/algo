package com.seungjoon.algo.member.repository;

import com.seungjoon.algo.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByUsername(String username);

    Optional<Member> findByEmail(String email);

    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}
