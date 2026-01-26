package org.cv.moa.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.cv.moa.domain.user.entity.Seeker;
import org.cv.moa.domain.user.repository.SeekerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeekerService {

    private final SeekerRepository seekerRepository;

    @Transactional
    public void updateProfile(Long userId, String nickname, String position, int careerYear, String previousCompany,
            String techStacks, String bio, String portfolioUrl) {
        Seeker seeker = seekerRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("구직자 정보 없음"));

        seeker.updateProfile(nickname, position, careerYear, previousCompany, techStacks, bio, portfolioUrl);
    }

    public Seeker getSeekerByUserId(Long userId) {
        return seekerRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("구직자 정보 없음"));
    }
}
