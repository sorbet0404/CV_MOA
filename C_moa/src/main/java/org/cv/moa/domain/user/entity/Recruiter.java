package org.cv.moa.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "recruiters")
public class Recruiter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recruiter_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String companyName;

    // ★ ERD 반영 추가 필드 ★
    @Column(columnDefinition = "TEXT")
    private String companyDesc; // 회사 소개

    private String companyLogoUrl; // 회사 로고 이미지 주소

    private Double rating; // 평점 (예: 4.5)

    private String industry; // 산업군 (IT, 금융 등)

    @Column(columnDefinition = "TEXT")
    private String companyPros; // 장점

    @Column(columnDefinition = "TEXT")
    private String companyCons; // 단점

    private String verifiedCode; // 인증 코드

    // [추가] 유료 정보 (기업 인사이트)
    @Column(columnDefinition = "TEXT")
    private String interviewTips; // 면접 팁 / 기출 질문

    @Column(columnDefinition = "TEXT")
    private String companyCulture; // 조직 문화 / 현직자 평가 상세

    private String salaryInfo; // 연봉 정보 (예: "신입 5,000만원 + @")

    public void updateCompanyInfo(String companyName, String industry, String companyDesc, String companyPros,
            String companyCons, Double rating, String companyLogoUrl) {
        this.companyName = companyName;
        this.industry = industry;
        this.companyDesc = companyDesc;
        this.companyPros = companyPros;
        this.companyCons = companyCons;
        this.rating = rating;
        this.companyLogoUrl = companyLogoUrl;
    }

    public void updatePremiumInfo(String interviewTips, String companyCulture, String salaryInfo) {
        this.interviewTips = interviewTips;
        this.companyCulture = companyCulture;
        this.salaryInfo = salaryInfo;
    }
}