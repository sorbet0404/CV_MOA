package org.cv.moa.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.cv.moa.domain.user.entity.Role;
import org.cv.moa.domain.user.entity.User;

@Getter
@Setter // DTO는 데이터 전달용이라 Setter 써도 괜찮습니다.
@NoArgsConstructor
public class SignupRequest {

    private String email;
    private String password;
    private String name;
    private Role role;    // "SEEKER" 또는 "RECRUITER"
    private String intro; // 한 줄 소개

    // DTO -> Entity 변환 메서드 (서비스 로직 깔끔하게 만들기 위함)
    public User toEntity(String encodedPassword) {
        return User.builder()
                .email(this.email)
                .password(encodedPassword) // 비밀번호는 반드시 암호화해서 넣어야 함
                .name(this.name)
                .role(this.role)
                .intro(this.intro)
                .build();
    }
}