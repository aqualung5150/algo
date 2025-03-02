package com.seungjoon.algo.submission.dto;

import com.seungjoon.algo.member.dto.ProfileResponse;
import com.seungjoon.algo.submission.domain.Evaluation;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EvaluationsResponse {

    private List<EvaluationResponse> evaluations;

    public static EvaluationsResponse from(List<Evaluation> evaluations) {

        return new EvaluationsResponse(evaluations.stream()
                .map(evaluation -> new EvaluationResponse(
                        evaluation.getId(),
                        evaluation.getContent(),
                        evaluation.getPassFail().name(),
                        ProfileResponse.from(evaluation.getMember())
                )).toList());
    }

    @Data
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    static class EvaluationResponse {

        private Long id;
        private String content;
        private String passFail;
        private ProfileResponse evaluator;
    }
}
