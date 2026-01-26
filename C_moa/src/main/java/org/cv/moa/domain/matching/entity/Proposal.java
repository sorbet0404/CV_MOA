package org.cv.moa.domain.matching.entity;

import jakarta.persistence.*;
import lombok.*;
import org.cv.moa.domain.job.entity.JobPosting;
import org.cv.moa.domain.user.entity.Seeker;
import org.hibernate.annotations.CreationTimestamp;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "proposals")
@EntityListeners(AuditingEntityListener.class)
public class Proposal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "proposal_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seeker_id", nullable = false)
    private Seeker seeker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private JobPosting jobPosting;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProposalType type; // APPLY(구직자가 지원), SCOUT(기업이 제안)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProposalStatus status; // WAITING, ACCEPTED, REJECTED

    private boolean isSuperLike; // [추가] 슈퍼 라이크 여부

    @CreationTimestamp
    private LocalDateTime createdAt;

    // 상태 변경 메서드
    public void accept() {
        this.status = ProposalStatus.ACCEPTED;
    }

    public void reject() {
        this.status = ProposalStatus.REJECTED;
    }
}
