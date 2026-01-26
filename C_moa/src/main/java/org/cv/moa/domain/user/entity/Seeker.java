package org.cv.moa.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "seekers")
public class Seeker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seeker_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String nickname;
    private String position;
    private int careerYear;
    private String previousCompany; // 경력직일 경우 이전 직장

    // ★ ERD 반영 추가 필드 ★
    private String techStacks; // 보유 기술

    @Column(columnDefinition = "TEXT")
    private String bio; // 자기소개

    @Column(columnDefinition = "TEXT")
    private String resumeContent; // 이력서 내용

    private String portfolioUrl; // 포트폴리오 링크

    public void updateProfile(String nickname, String position, int careerYear, String previousCompany,
            String techStacks, String bio, String portfolioUrl) {
        this.nickname = nickname;
        this.position = position;
        this.careerYear = careerYear;
        this.previousCompany = previousCompany;
        this.techStacks = techStacks;
        this.bio = bio;
        this.portfolioUrl = portfolioUrl;
    }
}