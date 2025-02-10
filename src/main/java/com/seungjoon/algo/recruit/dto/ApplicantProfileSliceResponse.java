package com.seungjoon.algo.recruit.dto;

import com.seungjoon.algo.member.dto.ProfileResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApplicantProfileSliceResponse {

    private Boolean hasNext;
    private List<ProfileResponse> applicantList;

    public static ApplicantProfileSliceResponse of(Boolean hasNext, List<ProfileResponse> applicantList) {
        return new ApplicantProfileSliceResponse(hasNext, applicantList);
    }
}
