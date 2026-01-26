package org.cv.moa.domain.company.service;

import lombok.RequiredArgsConstructor;
import org.cv.moa.domain.company.entity.CompanyUnlock;
import org.cv.moa.domain.company.repository.CompanyUnlockRepository;
import org.cv.moa.domain.user.entity.Recruiter;
import org.cv.moa.domain.user.entity.User;
import org.cv.moa.domain.user.repository.RecruiterRepository;
import org.cv.moa.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final RecruiterRepository recruiterRepository;
    private final CompanyUnlockRepository companyUnlockRepository;
    private final UserRepository userRepository;

    public Recruiter getRecruiter(Long id) {
        return recruiterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 기업입니다."));
    }

    public boolean isUnlocked(User user, Long recruiterId) {
        if (user == null)
            return false;
        return companyUnlockRepository.existsByUserIdAndRecruiterId(user.getId(), recruiterId);
    }

    @Transactional
    public void unlockCompany(User user, Long recruiterId) {
        Recruiter recruiter = getRecruiter(recruiterId);

        // 이미 잠금 해제했는지 확인
        if (companyUnlockRepository.existsByUserIdAndRecruiterId(user.getId(), recruiterId)) {
            return; // 이미 해제됨 (중복 차감 방지)
        }

        // 포인트 차감 (50P)
        int unlockCost = 50;

        // 포인트 차감 및 저장 (User 엔티티 메소드 사용)
        User persistentUser = userRepository.findById(user.getId()).orElseThrow();

        try {
            persistentUser.deductPoint(unlockCost);
            // 만약 세션 유저 객체도 업데이트가 필요하다면?
            // Controller에서 세션을 다시 로드하거나 업데이트해야 함.
            // 여기서는 영속성 컨텍스트만 처리.
        } catch (IllegalStateException e) {
            throw new IllegalStateException("point_shortage");
        }

        // 잠금 해제 기록 저장
        CompanyUnlock unlock = CompanyUnlock.builder()
                .user(persistentUser)
                .recruiter(recruiter)
                .build();

        companyUnlockRepository.save(unlock);
    }
}
