package org.cv.moa.domain.user.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.cv.moa.domain.user.entity.Seeker;
import org.cv.moa.domain.user.entity.User;
import org.cv.moa.domain.user.service.SeekerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller // "나는 화면(HTML)을 돌려주는 웨이터입니다"
@RequiredArgsConstructor
public class UserViewController {

    private final SeekerService seekerService;
    private final org.cv.moa.domain.user.repository.RecruiterRepository recruiterRepository;

    @GetMapping("/login")
    public String loginPage() {
        // templates/user/login.html 파일을 찾아서 보여줘라!
        // (뒤에 .html은 생략 가능)
        return "user/login";
    }

    @GetMapping("/signup")
    public String signupPage() {
        return "user/signup";
    }

    @GetMapping("/profile")
    public String profilePage(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null && user.getRole().toString().equals("SEEKER")) {
            Seeker seeker = seekerService.getSeekerByUserId(user.getId());
            model.addAttribute("seeker", seeker);
        }
        return "user/profile"; // templates/user/profile.html을 보여줌
    }

    @GetMapping("/recruiters/company-info")
    public String companyInfoPage(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null && user.getRole().toString().equals("RECRUITER")) {
            org.cv.moa.domain.user.entity.Recruiter recruiter = recruiterRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new IllegalArgumentException("채용 담당자 정보를 찾을 수 없습니다."));
            model.addAttribute("recruiter", recruiter);
            model.addAttribute("mode", "company-info"); // 헤더 제어용
            return "user/company-info";
        }
        return "redirect:/login";
    }
}