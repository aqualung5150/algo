package com.seungjoon.algo.study.service;

import com.seungjoon.algo.exception.BadRequestException;
import com.seungjoon.algo.member.domain.Member;
import com.seungjoon.algo.member.repository.MemberRepository;
import com.seungjoon.algo.study.domain.Study;
import com.seungjoon.algo.study.domain.StudyMember;
import com.seungjoon.algo.study.domain.StudyState;
import com.seungjoon.algo.study.repository.BanVoteRepository;
import com.seungjoon.algo.study.repository.StudyRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;

import static java.time.DayOfWeek.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class StudyServiceTest {

    @InjectMocks
    StudyService studyService;

    @Mock
    StudyRepository studyRepository;

    @Mock
    MemberRepository memberRepository;

    @Mock
    BanVoteRepository banVoteRepository;

    @Test
    void banVote() {
        //given
        Member voter = Member.builder()
                .username("voter")
                .email("voter@example.com")
                .build();
        ReflectionTestUtils.setField(voter, "id", 1L);

        Member target = Member.builder()
                .username("target")
                .email("target@example.com")
                .build();
        ReflectionTestUtils.setField(target, "id", 2L);

        Study study = Study.builder()
                .state(StudyState.IN_PROGRESS)
                .build();
        ReflectionTestUtils.setField(study, "id", 1L);

        study.addStudyMembers(List.of(
                StudyMember.builder().study(study).member(voter).build(),
                StudyMember.builder().study(study).member(target).build()
        ));

        given(studyRepository.findByIdJoinFetch(anyLong()))
                .willReturn(Optional.of(study));

        given(memberRepository.findById(1L)).willReturn(Optional.of(voter));
        given(memberRepository.findById(2L)).willReturn(Optional.of(target));

        given(banVoteRepository.existsByStudyIdAndVoterIdAndTargetId(anyLong(), anyLong(), anyLong()))
                .willReturn(false);

        //when

        //then
        studyService.banVote(study.getId(), voter.getId(), target.getId());
    }

    @Test
    void banSameVoterAndTarget() {
        assertThatThrownBy(() -> studyService.banVote(1L, 2L, 2L))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void voteNotInStudy() {
        //given
        Member voter = Member.builder()
                .username("voter")
                .email("voter@example.com")
                .build();
        ReflectionTestUtils.setField(voter, "id", 1L);

        Member target = Member.builder()
                .username("target")
                .email("target@example.com")
                .build();
        ReflectionTestUtils.setField(target, "id", 2L);

        Member notInStudy = Member.builder()
                .username("notInStudy")
                .email("notInStudy@example.com")
                .build();
        ReflectionTestUtils.setField(notInStudy, "id", 3L);

        Study study = Study.builder()
                .state(StudyState.IN_PROGRESS)
                .build();
        ReflectionTestUtils.setField(study, "id", 1L);

        study.addStudyMembers(List.of(
                StudyMember.builder().study(study).member(voter).build(),
                StudyMember.builder().study(study).member(target).build()
        ));

        given(studyRepository.findByIdJoinFetch(anyLong()))
                .willReturn(Optional.of(study));

        //when

        //then
        assertThatThrownBy(() -> studyService.banVote(study.getId(), voter.getId(), notInStudy.getId()))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void getCurrentWeek() {
        //given

        //2025-3-1 토요일
        LocalDate startSubmitDate = LocalDate.of(2025, 3, 1)
                .with(TemporalAdjusters.nextOrSame(SUNDAY));

        LocalDate firstSaturday = LocalDate.of(2025, 3, 1)
                .with(TemporalAdjusters.nextOrSame(SUNDAY));

        LocalDate sameWithStartSubmitDate = LocalDate.of(2025, 3, 2)
                .with(TemporalAdjusters.nextOrSame(SUNDAY));

        LocalDate thirdMonday = LocalDate.of(2025, 3, 10)
                .with(TemporalAdjusters.nextOrSame(SUNDAY));

        LocalDate thirdSunday = LocalDate.of(2025, 3, 16)
                .with(TemporalAdjusters.nextOrSame(SUNDAY));

        //when

        //then
        assertThat(getCurrentWeek(startSubmitDate, firstSaturday)).isEqualTo(1);
        assertThat(getCurrentWeek(startSubmitDate, sameWithStartSubmitDate)).isEqualTo(1);
        assertThat(getCurrentWeek(startSubmitDate, thirdMonday)).isEqualTo(3);
        assertThat(getCurrentWeek(startSubmitDate, thirdSunday)).isEqualTo(3);
    }

    private Integer getCurrentWeek(LocalDate startSubmitDate, LocalDate submitDate) {
        Integer weekNumber = 1;
        while (submitDate.isAfter(startSubmitDate)) {
            startSubmitDate = startSubmitDate.plusWeeks(1L);
            ++weekNumber;
        }
        return weekNumber;
    }
}