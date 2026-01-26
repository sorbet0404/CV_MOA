package org.cv.moa.domain.user.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cv.moa.domain.user.entity.User;
import org.cv.moa.domain.user.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserScheduler {

    private final UserRepository userRepository;

    // 매일 자정 (한국 시간) 실행
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    @Transactional
    public void giveDailyPoints() {
        log.info("Starting daily point reward...");
        List<User> users = userRepository.findAll();
        for (User user : users) {
            user.addPoint(10); // 하루 10포인트 지급
        }
        log.info("Finished awarding daily points to {} users.", users.size());
    }
}
