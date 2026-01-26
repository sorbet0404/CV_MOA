package org.cv.moa.domain.chat.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.cv.moa.domain.job.entity.JobPosting; // JobPosting 엔티티 필요
import org.cv.moa.domain.user.entity.Seeker;     // Seeker 엔티티 필요
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "matches")
@EntityListeners(AuditingEntityListener.class)
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "match_id")
    private Long matchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seeker_id")
    private Seeker seeker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id")
    private JobPosting jobPosting;

    @CreatedDate
    private LocalDateTime matchedAt;

    // 생성자
    public Match(Seeker seeker, JobPosting jobPosting) {
        this.seeker = seeker;
        this.jobPosting = jobPosting;
    }
}