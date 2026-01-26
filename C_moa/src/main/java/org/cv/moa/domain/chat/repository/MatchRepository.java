package org.cv.moa.domain.chat.repository;

import org.cv.moa.domain.chat.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchRepository extends JpaRepository<Match, Long> {
    java.util.List<Match> findByJobPosting(org.cv.moa.domain.job.entity.JobPosting jobPosting);
}
