package org.cv.moa.domain.user.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.cv.moa.domain.user.dto.LoginRequest;
import org.cv.moa.domain.user.dto.SignupRequest;
import org.cv.moa.domain.user.entity.User;
import org.cv.moa.domain.user.service.UserService;
import org.cv.moa.domain.user.service.SeekerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final SeekerService seekerService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequest request) {
        Long userId = userService.signup(request);
        return ResponseEntity.ok("회원가입 성공! (ID: " + userId + ")");
    }

    /**
     * [수정된 로그인 기능]
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request, HttpSession session) {
        User user = userService.login(request);
        session.setAttribute("user", user);
        String userRole = user.getRole().toString();
        return ResponseEntity.ok(userRole);
    }

    @PostMapping("/profile")
    public ResponseEntity<String> updateProfile(
            @RequestBody org.cv.moa.domain.user.dto.SeekerProfileUpdateRequest request, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        if (user.getRole() == org.cv.moa.domain.user.entity.Role.SEEKER) {
            seekerService.updateProfile(user.getId(),
                    request.getNickname(),
                    request.getPosition(),
                    request.getCareerYear(),
                    request.getPreviousCompany(),
                    request.getTechStacks(),
                    request.getBio(),
                    request.getPortfolioUrl());
            return ResponseEntity.ok("프로필 수정 완료");
        }

        return ResponseEntity.badRequest().body("구직자만 이용 가능합니다.");
    }

    @PostMapping("/recruiters/company-info")
    public ResponseEntity<String> updateCompanyInfo(
            @RequestBody org.cv.moa.domain.user.dto.CompanyInfoUpdateRequest request, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        if (user.getRole() == org.cv.moa.domain.user.entity.Role.RECRUITER) {
            userService.updateCompanyInfo(user, request);
            return ResponseEntity.ok("회사 정보 수정 완료");
        }

        return ResponseEntity.badRequest().body("채용 담당자만 이용 가능합니다.");
    }
}
