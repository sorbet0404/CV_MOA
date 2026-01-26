package org.cv.moa.domain.company.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.cv.moa.domain.company.service.CompanyService;
import org.cv.moa.domain.user.entity.Recruiter;
import org.cv.moa.domain.user.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;
    private final org.cv.moa.domain.user.repository.UserRepository userRepository; // [추가]

    @GetMapping("/company/{id}")
    public String companyDetail(@PathVariable Long id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null)
            return "redirect:/login";

        Recruiter recruiter = companyService.getRecruiter(id);
        boolean isUnlocked = companyService.isUnlocked(user, id);

        model.addAttribute("recruiter", recruiter);
        model.addAttribute("isUnlocked", isUnlocked);

        return "company/detail";
    }

    @PostMapping("/company/{id}/unlock")
    @ResponseBody
    public String unlockCompany(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null)
            return "login_required";

        try {
            companyService.unlockCompany(user, id);

            // [Fix] 세션 동기화
            User updatedUser = userRepository.findById(user.getId()).orElse(user);
            session.setAttribute("user", updatedUser);

            return "success";
        } catch (IllegalStateException e) {
            if ("point_shortage".equals(e.getMessage())) {
                return "point_shortage";
            }
            return "fail";
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }
}
