package org.cv.moa.domain.job.entity;

import jakarta.persistence.*;
import lombok.*;
import org.cv.moa.domain.user.entity.Recruiter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "job_postings")
public class JobPosting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_id") // ✅ DB가 job_id 인 경우 필수 (너가 겪은 jp1_0.id 에러 해결)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruiter_id")
    private Recruiter recruiter;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String requirements; // 자격 요건

    @Column(columnDefinition = "TEXT")
    private String preferences; // [복구] 우대사항

    private String techStacks; // 기술 스택 (예: "Java, Spring, MySQL")

    @Column(columnDefinition = "DATE")
    private LocalDate startDate; // 공고 시작일

    // [수정] 마감 시간까지 관리 (자동 마감을 위해)
    private LocalDateTime endDate; // 공고 마감일 (NULL이면 상시 채용)

    @Column(nullable = false)
    private String status; // OPEN, CLOSED

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt; // 생성일 자동 기록

    // ✅ 1-B: 수정 기능을 위한 업데이트 메서드
    public void update(String title, String description, String requirements, String preferences, String techStacks,
            LocalDate startDate, LocalDateTime endDate) {
        this.title = title;
        this.description = description;
        this.requirements = requirements;
        this.preferences = preferences;
        this.techStacks = techStacks;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // ✅ 1-B: 취소(마감) 기능
    public void close() {
        this.status = "CLOSED";
    }

    // [추가] 다시 열기 기능
    public void reopen() {
        this.status = "OPEN";
    }
}
