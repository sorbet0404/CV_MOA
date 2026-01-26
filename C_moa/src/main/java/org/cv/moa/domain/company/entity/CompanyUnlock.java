package org.cv.moa.domain.company.entity;

import jakarta.persistence.*;
import lombok.*;
import org.cv.moa.domain.user.entity.Recruiter;
import org.cv.moa.domain.user.entity.User;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "company_unlocks", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "user_id", "recruiter_id" })
})
@EntityListeners(AuditingEntityListener.class)
public class CompanyUnlock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // 열람한 구직자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruiter_id")
    private Recruiter recruiter; // 열람 대상 기업

    @CreatedDate
    private LocalDateTime unlockedAt;
}
