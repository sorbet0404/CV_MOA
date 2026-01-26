package org.cv.moa.domain.matching.repository;

import org.cv.moa.domain.job.entity.JobPosting;
import org.cv.moa.domain.matching.entity.Proposal;
import org.cv.moa.domain.matching.entity.ProposalStatus;
import org.cv.moa.domain.matching.entity.ProposalType;

import org.cv.moa.domain.user.entity.Seeker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProposalRepository extends JpaRepository<Proposal, Long> {

        void deleteByJobPosting(JobPosting jobPosting);

        // 특정 구직자와 공고 사이의 제안 조회
        Optional<Proposal> findBySeekerAndJobPosting(Seeker seeker, JobPosting jobPosting);

        // 내가 받은 제안 조회 (구직자 입장: 기업이 보낸 SCOUT) - 슈퍼라이크 우선 정렬
        List<Proposal> findBySeekerAndTypeAndStatusOrderByIsSuperLikeDescCreatedAtDesc(Seeker seeker, ProposalType type,
                        ProposalStatus status);

        // 내가 받은 제안 조회 (기업 입장: 구직자가 보낸 APPLY)
        @Query("SELECT p FROM Proposal p " +
                        "JOIN FETCH p.jobPosting j " +
                        "JOIN FETCH j.recruiter r " +
                        "JOIN FETCH p.seeker s " +
                        "WHERE r.id = :recruiterId AND p.type = :type AND p.status = :status " +
                        "ORDER BY p.isSuperLike DESC, p.createdAt DESC") // [수정] 슈퍼라이크 우선 정렬
        List<Proposal> findRecruiterProposals(@Param("recruiterId") Long recruiterId, @Param("type") ProposalType type,
                        @Param("status") ProposalStatus status);

        // 이미 제안을 보냈거나 받은 이력이 있는 구직자 ID 목록 (기업의 탐색 필터링용)
        @Query("SELECT p.seeker.id FROM Proposal p WHERE p.jobPosting.recruiter.id = :recruiterId")
        List<Long> findInteractedSeekerIds(@Param("recruiterId") Long recruiterId);

        // 이미 지원했거나 제안받은 이력이 있는 공고 ID 목록 (구직자의 탐색 필터링용)
        @Query("SELECT p.jobPosting.id FROM Proposal p WHERE p.seeker.id = :seekerId")
        List<Long> findInteractedJobIds(@Param("seekerId") Long seekerId);
}
