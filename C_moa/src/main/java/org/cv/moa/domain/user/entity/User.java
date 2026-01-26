package org.cv.moa.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "users") // DB 예약어 방지
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role; // SEEKER, RECRUITER

    // [추가] 포인트 (기본값 0)
    @Column(nullable = false)
    private int point = 0;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public void addPoint(int amount) {
        this.point += amount;
    }

    public void deductPoint(int amount) {
        if (this.point < amount) {
            throw new IllegalStateException("포인트가 부족합니다.");
        }
        this.point -= amount;
    }

    private String intro; // 한 줄 소개

    @Builder
    public User(String email, String password, String name, Role role, String intro) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
        this.intro = intro;
        // point field is initialized to 0 by default and not passed in the builder
        // constructor
    }
}