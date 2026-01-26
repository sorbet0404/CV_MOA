package org.cv.moa.domain.job.service;

import lombok.RequiredArgsConstructor;
import org.cv.moa.domain.job.entity.JobPosting;
import org.cv.moa.domain.job.repository.JobPostingRepository;
import org.cv.moa.domain.user.entity.Recruiter;
import org.cv.moa.domain.user.entity.User;
import org.cv.moa.domain.user.repository.RecruiterRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JobPostingService {

    private final JobPostingRepository jobPostingRepository;
    private final RecruiterRepository recruiterRepository;

    @Transactional
    public Long createJobPosting(User user, String title, String description, String requirements, String preferences,
            String techStacks, LocalDate startDate, java.time.LocalDateTime endDate) {
        Recruiter recruiter = recruiterRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("기업 회원 정보를 찾을 수 없습니다."));

        JobPosting jobPosting = JobPosting.builder()
                .recruiter(recruiter)
                .title(title)
                .description(description)
                .requirements(requirements)
                .preferences(preferences)
                .techStacks(techStacks)
                .startDate(startDate)
                .endDate(endDate)
                .status("OPEN")
                .build();

        return jobPostingRepository.save(jobPosting).getId();
    }

    public List<JobPosting> getOpenJobPostings() {
        return jobPostingRepository.findByStatus("OPEN");
    }

    public JobPosting getJobPosting(Long id) {
        return jobPostingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Job Posting not found for id: " + id));
    }

    @Transactional
    public void updateJobPosting(Long id, String title, String description, String requirements, String preferences,
            String techStacks, LocalDate startDate, java.time.LocalDateTime endDate) {
        JobPosting jobPosting = jobPostingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공고입니다."));

        jobPosting.update(title, description, requirements, preferences, techStacks, startDate, endDate);
    }

    // [추가] 마감 시간 지난 공고 자동 종료 스케줄러 (1분마다 실행)
    @Transactional
    @org.springframework.scheduling.annotation.Scheduled(fixedRate = 60000)
    public void closeExpiredJobs() {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        List<JobPosting> expiredJobs = jobPostingRepository.findByStatusAndEndDateBefore("OPEN", now);

        if (!expiredJobs.isEmpty()) {
            System.out.println(">>> [Scheduler] Found " + expiredJobs.size() + " expired jobs. Closing them...");
            for (JobPosting job : expiredJobs) {
                job.close();
                System.out.println("    - Closed job: " + job.getTitle() + " (End Date: " + job.getEndDate() + ")");
            }
        }
    }

    @Transactional
    public void closeJobPosting(Long id) {
        JobPosting jobPosting = getJobPosting(id);
        jobPosting.close();
    }

    @Transactional
    public void reopenJobPosting(Long id) {
        JobPosting jobPosting = getJobPosting(id);
        jobPosting.reopen();
    }

    public List<JobPosting> getJobPostingsForUser(User user) {
        if (user != null && user.getRole().name().equals("RECRUITER")) {
            Recruiter recruiter = recruiterRepository.findByUserId(user.getId())
                    .orElse(null);
            if (recruiter != null) {
                return jobPostingRepository.findByRecruiterId(recruiter.getId());
            }
        }
        return jobPostingRepository.findByStatus("OPEN");
    }

    @Transactional
    public void deleteJobPosting(Long id) {
        JobPosting jobPosting = getJobPosting(id);

        // 1. 매칭 및 채팅방 삭제
        org.cv.moa.domain.chat.repository.MatchRepository matchRepository = applicationContext
                .getBean(org.cv.moa.domain.chat.repository.MatchRepository.class);
        org.cv.moa.domain.chat.repository.ChatRoomRepository chatRoomRepository = applicationContext
                .getBean(org.cv.moa.domain.chat.repository.ChatRoomRepository.class);

        List<org.cv.moa.domain.chat.entity.Match> matches = matchRepository.findByJobPosting(jobPosting);
        for (org.cv.moa.domain.chat.entity.Match match : matches) {
            // 채팅방이 있으면 삭제
            chatRoomRepository.findByMatch(match).ifPresent(chatRoomRepository::delete);
            matchRepository.delete(match);
        }

        // 2. 제안(Proposal) 삭제
        org.cv.moa.domain.matching.repository.ProposalRepository proposalRepository = applicationContext
                .getBean(org.cv.moa.domain.matching.repository.ProposalRepository.class);
        proposalRepository.deleteByJobPosting(jobPosting);

        // 3. 공고 삭제
        jobPostingRepository.delete(jobPosting);
    }

    @org.springframework.beans.factory.annotation.Autowired
    private org.springframework.context.ApplicationContext applicationContext;
}
