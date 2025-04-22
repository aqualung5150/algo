package com.seungjoon.algo.member.service;

import com.seungjoon.algo.exception.BadRequestException;
import com.seungjoon.algo.exception.ExceptionCode;
import com.seungjoon.algo.image.domain.Image;
import com.seungjoon.algo.image.repository.ImageRepository;
import com.seungjoon.algo.member.domain.Member;
import com.seungjoon.algo.member.dto.UpdateMemberRequest;
import com.seungjoon.algo.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.seungjoon.algo.exception.ExceptionCode.NOT_FOUND_MEMBER;
import static com.seungjoon.algo.exception.ExceptionCode.USERNAME_ALREADY_EXIST;
import static com.seungjoon.algo.image.domain.ImageType.PROFILE;
import static com.seungjoon.algo.image.domain.ImageType.TEMPORARY;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final ImageRepository imageRepository;

    public Member getById(Long id) {

        return memberRepository
                .findById(id)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER));
    }

    @Transactional
    public Member updateById(Long id, UpdateMemberRequest request) {
        Member member = getById(id);

        duplicateUsername(member.getUsername(), request.getUsername());

        //Update Image
        if (!member.getImageUrl().equals(request.getImageUrl())) {
            //Delete Previous Image
            imageRepository.findByMember(member).ifPresent(image -> {
                image.changeType(TEMPORARY);
                image.changeMember(null);
            });

            //New Image
            String[] split = request.getImageUrl().split("/");
            String newImageId = split[split.length - 1];
            Image newImage = imageRepository.findById(newImageId).orElseThrow(() -> new BadRequestException(ExceptionCode.MISSING_JWT_TOKEN));
            newImage.changeType(PROFILE);
            newImage.changeMember(member);
        }

        member.updateMember(request.getUsername(), request.getImageUrl());

        return member;
    }

    private void duplicateUsername(String from, String to) {
        if (!from.equals(to) && memberRepository.existsByUsername(to)) {
            throw new BadRequestException(USERNAME_ALREADY_EXIST);
        }
    }
}
