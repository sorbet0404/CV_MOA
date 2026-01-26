package org.cv.moa.global.init;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.cv.moa.domain.job.entity.JobPosting;
import org.cv.moa.domain.job.repository.JobPostingRepository;
import org.cv.moa.domain.user.entity.Recruiter;
import org.cv.moa.domain.user.entity.Role;
import org.cv.moa.domain.user.entity.Seeker;
import org.cv.moa.domain.user.entity.User;
import org.cv.moa.domain.user.repository.RecruiterRepository;
import org.cv.moa.domain.user.repository.SeekerRepository;
import org.cv.moa.domain.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DataInit {

    private final UserRepository userRepository;
    private final SeekerRepository seekerRepository;
    private final RecruiterRepository recruiterRepository;
    private final JobPostingRepository jobPostingRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    @Transactional
    public void init() {
        if (userRepository.count() > 0) return;

        // 1. 구직자 생성
        User user1 = User.builder()
                .email("seeker@test.com")
                .password(passwordEncoder.encode("1234"))
                .name("김이름")
                .role(Role.SEEKER)
                .intro("안녕하세요, 열정적인 개발자입니다.")
                .build();
        userRepository.save(user1);

        Seeker seeker1 = Seeker.builder()
                .user(user1)
                .nickname("김개발")
                .position("백엔드 개발자")
                .careerYear(3)
                .techStacks("Java, Spring Boot, JPA")
                .bio("꾸준히 성장하는 거북이 개발자입니다.")
                .portfolioUrl("https://github.com/my-portfolio")
                .build();
        seekerRepository.save(seeker1);

        // 2. 채용 담당자 생성
        User user2 = User.builder()
                .email("recruiter@naver.com")
                .password(passwordEncoder.encode("1234"))
                .name("박인사")
                .role(Role.RECRUITER)
                .intro("네이버 채용 담당자입니다.")
                .build();
        userRepository.save(user2);

        Recruiter recruiter1 = Recruiter.builder()
                .user(user2)
                .companyName("네이버 (Naver)")
                .companyDesc("대한민국 최고의 검색 포털")
                .industry("IT/Platform")
                .rating(4.8)
                .build();
        recruiterRepository.save(recruiter1);

        // 3. 채용 공고 생성
        JobPosting job1 = JobPosting.builder()
                .recruiter(recruiter1)
                .title("백엔드 신입/경력 채용")
                .description("Spring Boot 대규모 트래픽 경험자 우대")
                .requirements("Java 17 이상 경험자")
                .techStacks("Spring Boot, MySQL, Redis")
                .status("OPEN")
                .build();
        jobPostingRepository.save(job1);

        JobPosting job2 = JobPosting.builder()
                .recruiter(recruiter1)
                .title("프론트엔드 개발자 모집")
                .description("React, TypeScript 능숙하신 분")
                .requirements("React 3년 이상")
                .techStacks("React, TypeScript, Next.js")
                .status("OPEN")
                .build();
        jobPostingRepository.save(job2);

        System.out.println("✅ DB 구조 업데이트 및 데이터 초기화 완료!");
    }
}