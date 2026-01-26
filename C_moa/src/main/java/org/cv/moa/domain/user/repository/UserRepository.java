package org.cv.moa.domain.user.repository;

import org.cv.moa.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // 로그인할 때 이메일로 회원을 찾아야 하므로 이 메서드가 필수입니다.
    // "select * from users where email = ?" 쿼리를 자동으로 만들어줍니다.
    Optional<User> findByEmail(String email);

    // 중복 가입 방지용
    boolean existsByEmail(String email);
}