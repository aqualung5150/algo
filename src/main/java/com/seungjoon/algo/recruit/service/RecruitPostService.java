package com.seungjoon.algo.recruit.service;

import com.seungjoon.algo.exception.BadRequestException;
import com.seungjoon.algo.exception.ExceptionCode;
import com.seungjoon.algo.member.domain.Member;
import com.seungjoon.algo.member.repository.MemberRepository;
import com.seungjoon.algo.recruit.domain.RecruitPost;
import com.seungjoon.algo.recruit.dto.CreateRecruitPostRequest;
import com.seungjoon.algo.recruit.repository.RecruitPostRepository;
import com.seungjoon.algo.study.domain.StudyRule;
import com.seungjoon.algo.study.repository.StudyRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.List;

import static com.seungjoon.algo.exception.ExceptionCode.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RecruitPostService {

    private final RecruitPostRepository recruitPostRepository;
    private final StudyRuleRepository studyRuleRepository;
    private final MemberRepository memberRepository;

    //TODO - DTO
//    public List<RecruitPost> findAll() {
//        return recruitPostRepository.findAll();
//    }

    @Transactional
    public RecruitPost create(Long memberId, CreateRecruitPostRequest request) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER));

        StudyRule studyRule = studyRuleRepository.save(StudyRule.builder()
                .submitDayOfWeek(DayOfWeek.valueOf(request.getSubmitDayOfWeek()))
                .totalWeek(request.getTotalWeek())
                .submitPerWeek(request.getSubmitPerWeek())
                .build()
        );

        return recruitPostRepository.save(RecruitPost.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .studyRule(studyRule)
                .member(member)
                .build()
        );
    }

    public RecruitPost getById(Long id) {
        return recruitPostRepository.findById(id).orElseThrow(() -> new BadRequestException(NOT_FOUND_POST));
    }
}
