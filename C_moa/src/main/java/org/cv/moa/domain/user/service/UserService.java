package org.cv.moa.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.cv.moa.domain.user.dto.CompanyInfoUpdateRequest;
import org.cv.moa.domain.user.dto.LoginRequest;
import org.cv.moa.domain.user.dto.SignupRequest;
import org.cv.moa.domain.user.entity.Recruiter;
import org.cv.moa.domain.user.entity.Role;
import org.cv.moa.domain.user.entity.Seeker;
import org.cv.moa.domain.user.entity.User;
import org.cv.moa.domain.user.repository.RecruiterRepository;
import org.cv.moa.domain.user.repository.SeekerRepository;
import org.cv.moa.domain.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final RecruiterRepository recruiterRepository;
    private final SeekerRepository seekerRepository; // [추가]
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원가입 기능
     */
    @Transactional
    public Long signup(SignupRequest request) {
        // 1. 중복 이메일 검사
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        // 2. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 3. User 생성 및 포인트 지급
        User newUser = request.toEntity(encodedPassword);
        newUser.addPoint(10000); // [추가] 가입 축하 포인트

        // 4. DB 저장
        userRepository.save(newUser);

        // [추가] Role에 따라 세부 엔티티 생성
        if (newUser.getRole() == Role.SEEKER) {
            Seeker seeker = Seeker.builder()
                    .user(newUser)
                    .nickname(newUser.getName()) // 이름으로 닉네임 초기화
                    .position("Junior Developer") // 기본값
                    .build();
            seekerRepository.save(seeker);
        } else if (newUser.getRole() == Role.RECRUITER) {
            Recruiter recruiter = Recruiter.builder()
                    .user(newUser)
                    .companyName("My Company") // 기본값
                    .build();
            recruiterRepository.save(recruiter);
        }

        return newUser.getId();
    }

    @Transactional(readOnly = true)
    public User login(LoginRequest request) {
        // 1. 이메일로 유저 찾기
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));

        // 2. 비밀번호 확인
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return user;
    }

    /**
     * 채용 담당자 - 회사 정보 수정
     */
    @Transactional
    public void updateCompanyInfo(User user, CompanyInfoUpdateRequest request) {
        Recruiter recruiter = recruiterRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("채용 담당자 정보를 찾을 수 없습니다."));

        recruiter.updateCompanyInfo(
                request.getCompanyName(),
                request.getIndustry(),
                request.getCompanyDesc(),
                request.getCompanyPros(),
                request.getCompanyCons(),
                request.getRating(),
                request.getCompanyLogoUrl());
    }
}