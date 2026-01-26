package org.cv.moa.domain.job.controller;

import lombok.RequiredArgsConstructor;
import org.cv.moa.domain.job.entity.JobPosting;
import org.cv.moa.domain.job.service.JobPostingService;
import org.cv.moa.domain.user.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.servlet.http.HttpSession;

import java.util.List;

@Controller
@RequestMapping("/jobs")
@RequiredArgsConstructor
public class JobPostingController {

    private final JobPostingService jobPostingService;

    // 1. 채용 공고 전체 목록 페이지 (구직자용 - OPEN 상태만 조회 / 기업용 - 전체 조회)
    @GetMapping({ "", "/list" })
    public String list(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        List<JobPosting> jobPostings = jobPostingService.getJobPostingsForUser(user);
        model.addAttribute("jobPostings", jobPostings);

        return "job/list"; // templates/job/list.html 로 이동
    }

    // 2. 채용 공고 작성 페이지 보여주기
    @GetMapping("/write")
    public String writeForm() {
        return "job/write"; // templates/job/write.html 로 이동
    }

    // 3. 채용 공고 실제 저장하기
    @PostMapping("/write")
    public String write(String title, String description, String requirements, String preferences, String techStacks,
            java.time.LocalDate startDate,
            @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime endDate,
            HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null)
            return "redirect:/login";

        jobPostingService.createJobPosting(user, title, description, requirements, preferences, techStacks, startDate,
                endDate);

        return "redirect:/jobs/list";
    }

    // 공고 수정 화면
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        JobPosting posting = jobPostingService.getJobPosting(id);
        model.addAttribute("posting", posting);
        return "job/edit";
    }

    // 공고 수정 저장
    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long id,
            String title, String description, String requirements, String preferences, String techStacks,
            java.time.LocalDate startDate,
            @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime endDate) {
        jobPostingService.updateJobPosting(id, title, description, requirements, preferences, techStacks, startDate,
                endDate);
        return "redirect:/jobs/list";
    }

    // 공고 취소(= CLOSED 처리)
    @PostMapping("/{id}/close")
    public String close(@PathVariable Long id) {
        jobPostingService.closeJobPosting(id);
        return "redirect:/jobs/list";
    }

    // 공고 다시 열기(= OPEN 처리)
    @PostMapping("/{id}/reopen")
    public String reopen(@PathVariable Long id) {
        jobPostingService.reopenJobPosting(id);
        return "redirect:/jobs/list";
    }

    // [추가] 공고 삭제
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        jobPostingService.deleteJobPosting(id);
        return "redirect:/jobs/list";
    }
}
