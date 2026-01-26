package org.cv.moa.domain.job.repository;

import org.cv.moa.domain.job.entity.JobPosting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobPostingRepository extends JpaRepository<JobPosting, Long> {
    List<JobPosting> findByStatus(String status); // ✅ 2-B: 구직자 화면에서 OPEN 공고만 조회

    List<JobPosting> findByRecruiterId(Long recruiterId);

    List<JobPosting> findByRecruiterAndStatus(org.cv.moa.domain.user.entity.Recruiter recruiter, String status);

    // [추가] 마감 시간이 지난 공고 조회
    List<JobPosting> findByStatusAndEndDateBefore(String status, java.time.LocalDateTime endDate);

}
