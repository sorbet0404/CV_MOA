package org.cv.moa.domain.matching.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.cv.moa.domain.job.entity.JobPosting;
import org.cv.moa.domain.job.service.JobPostingService;
import org.cv.moa.domain.matching.entity.Proposal;
import org.cv.moa.domain.matching.service.MatchingService;
import org.cv.moa.domain.user.entity.Seeker;
import org.cv.moa.domain.user.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MatchingController {

    private final MatchingService matchingService;
    private final JobPostingService jobPostingService;
    private final org.cv.moa.domain.user.repository.UserRepository userRepository; // [추가]

    // 1. 구직자용: 일자리 탐색 (Swipe)
    @GetMapping("/matching")
    public String matchingPage(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null)
            return "redirect:/login";

        System.out.println("=================================================");
        System.out.println(">>> [DEBUG] 현재 로그인 계정: " + user.getEmail() + " (" + user.getName() + ")");

        // [수정] 이미 상호작용한 공고 제외
        List<JobPosting> jobPostings = matchingService.getJobPostingsToSwipe(user);

        System.out.println(">>> [DEBUG] 조회된 공고 개수: " + jobPostings.size());
        if (jobPostings.isEmpty()) {
            System.out.println(">>> [DEBUG] 🚨 공고가 하나도 조회되지 않았습니다! (이유: 모두 봤거나, DB에 공고가 없거나)");
        } else {
            System.out.println(">>> [DEBUG] 조회된 기업 목록:");
            for (JobPosting job : jobPostings) {
                System.out.println("    - [기업] " + job.getRecruiter().getCompanyName() + " / [제목] " + job.getTitle());
            }
        }
        System.out.println("=================================================");

        model.addAttribute("jobPostings", jobPostings);
        model.addAttribute("chatRooms", matchingService.getChatRooms(user));
        model.addAttribute("userRole", user.getRole().name());

        return "matching/swipe";
    }

    // 2. 기업회원용: 인재 탐색 (Swipe) - '안 본 사람'만 노출
    @GetMapping("/matching/recruiter")
    public String recruiterMatchingPage(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null)
            return "redirect:/login";

        System.out.println(">>> DEBUG: Recruiter Matching Page Entered. User: " + user.getEmail());

        List<Seeker> seekers = matchingService.getSeekersToSwipe(user);

        System.out.println(">>> DEBUG: Seekers Found: " + seekers.size());
        if (!seekers.isEmpty()) {
            seekers.forEach(s -> System.out.println("   - Seeker: " + s.getNickname()));
        }

        model.addAttribute("seekers", seekers);
        model.addAttribute("chatRooms", matchingService.getChatRooms(user));
        model.addAttribute("userRole", user.getRole().name());

        return "matching/swipe-recruiter";
    }

    // 3. 좋아요(Propose) API
    @PostMapping("/matches/propose")
    public String propose(@RequestParam Long targetId,
            @RequestParam(required = false) Long jobId,
            @RequestParam(defaultValue = "true") boolean isLike,
            HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null)
            return "redirect:/login";

        matchingService.propose(user, targetId, jobId, isLike);

        if (user.getRole().name().equals("RECRUITER")) {
            return "redirect:/matching/recruiter";
        } else {
            return "redirect:/matching";
        }
    }

    // 좋아요 / 싫어요 (제안 보내기)
    @PostMapping("/propose")
    @ResponseBody
    public String propose(@RequestParam Long targetId,
            @RequestParam(defaultValue = "true") boolean isLike,
            @RequestParam(defaultValue = "false") boolean isSuperLike,
            HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null)
            return "login";

        System.out.println(">>> [DEBUG] Proposal Request Received!");
        System.out.println("   - User: " + user.getEmail());
        System.out.println("   - TargetId: " + targetId);
        System.out.println("   - isLike: " + isLike);
        System.out.println("   - isSuperLike: " + isSuperLike);

        try {
            // isLike, isSuperLike 전달
            matchingService.propose(user, targetId, isLike, isSuperLike);

            // [Fix] 세션 유저 정보 갱신 (포인트 차감 반영)
            User updatedUser = userRepository.findById(user.getId()).orElse(user);
            session.setAttribute("user", updatedUser);

            return "success";
        } catch (IllegalStateException e) {
            return "point_shortage"; // 포인트 부족 시 에러 코드
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }

    // 4. 제안함 (나에게 온 좋아요 목록)
    @GetMapping("/matching/proposals")
    public String proposals(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null)
            return "redirect:/login";

        List<Proposal> proposals;
        if (user.getRole().name().equals("SEEKER")) {
            proposals = matchingService.getProposalsForSeeker(user);
        } else {
            proposals = matchingService.getProposalsForRecruiter(user);
        }
        model.addAttribute("proposals", proposals);
        model.addAttribute("chatRooms", matchingService.getChatRooms(user));
        model.addAttribute("userRole", user.getRole().name());

        return "matching/proposals";
    }
}
