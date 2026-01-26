package org.cv.moa.domain.company.repository;

import org.cv.moa.domain.company.entity.CompanyUnlock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyUnlockRepository extends JpaRepository<CompanyUnlock, Long> {
    Optional<CompanyUnlock> findByUserIdAndRecruiterId(Long userId, Long recruiterId);

    boolean existsByUserIdAndRecruiterId(Long userId, Long recruiterId);
}
