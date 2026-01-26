package org.cv.moa.common.init;

import lombok.RequiredArgsConstructor;
import org.cv.moa.domain.job.entity.JobPosting;
import org.cv.moa.domain.job.repository.JobPostingRepository;
import org.cv.moa.domain.user.entity.Recruiter;
import org.cv.moa.domain.user.entity.Role;
import org.cv.moa.domain.user.entity.User;
import org.cv.moa.domain.user.repository.RecruiterRepository;
import org.cv.moa.domain.user.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List; // [추가]

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

        private final UserRepository userRepository;
        private final RecruiterRepository recruiterRepository;
        private final org.cv.moa.domain.user.repository.SeekerRepository seekerRepository;
        private final JobPostingRepository jobPostingRepository;
        private final org.cv.moa.domain.matching.repository.ProposalRepository proposalRepository; // [추가]
        private final org.cv.moa.domain.chat.repository.MatchRepository matchRepository; // [추가]
        private final org.cv.moa.domain.chat.repository.ChatRoomRepository chatRoomRepository; // [추가]
        private final PasswordEncoder passwordEncoder;

        @Override
        public void run(String... args) throws Exception {
                // 이전 코드 유지...

                // [요청] 모든 매칭 관련 데이터 삭제 (채팅방 -> 매칭 -> 제안 순서로 삭제 해야 FK 제약 안걸림)
                System.out.println(">>> Cleaning up matching data...");
                chatRoomRepository.deleteAll();
                matchRepository.deleteAll();
                proposalRepository.deleteAll();
                System.out.println(">>> Matching data cleared!");

                System.out.println(">>> Initializing Dummy Data for Company Insight...");

                // (기존 기업 생성 코드 생략 없이 유지하려면 여기서는 추가만 해야 함)
                // 하지만 replace_file_content는 범위를 덮어쓰므로, run 메서드 내부 앞부분에 추가하면 뒤가 날아갈 수 있음.
                // 따라서 createCompany 호출 뒤에 추가하는 것이 안전함. (아래 별도 툴 호출로 진행)
                // if (recruiterRepository.count() > 0) {
                // return; // 이미 데이터가 있으면 스킵
                // }

                System.out.println(">>> Initializing Dummy Data for Company Insight...");

                // 1. ConnectFly (Toss)
                createCompany("ConnectFly", "금융 플랫폼", "자유로운 금융을 만듭니다.",
                                "완전 자율 출퇴근, 점심/저녁 식대 무제한, 무제한 휴가",
                                "빡센 업무 강도, 치열한 성장 환경", 4.8,
                                "https://cdn-icons-png.flaticon.com/512/3596/3596091.png",
                                "Q. 우리 은행 앱과 ConnectFly 앱의 가장 큰 차이점은 무엇이라 생각하나?\nQ. 가장 힘들었던 트러블슈팅 경험은?\n\nTip: 기술적인 깊이도 중요하지만, 사용자에 대한 집착(Customer Obsession)을 보여주는 것이 합격의 열쇠입니다.",
                                "최고의 동료들과 함께 일할 수 있음. 하지만 워라밸은 기대하지 않는 것이 좋음. 성장에 미친 사람들에게는 천국.");

                // 2. GreenSpace (Naver)
                createCompany("GreenSpace", "검색 포털 & AI", "세상의 모든 정보를 연결합니다.",
                                "사내 어린이집, 3년 근속 시 리프레시 휴가 1개월, 셔틀버스 운행",
                                "대기업 특유의 복잡한 보고 체계, 팀바팀 심함", 4.5,
                                "https://cdn-icons-png.flaticon.com/512/3983/3983877.png",
                                "Q. 대용량 트래픽 처리를 위한 아키텍처 설계 경험이 있나?\nQ. 검색 엔진의 원리에 대해 설명해보시오.\n\nTip: CS 기본기가 매우 탄탄해야 합니다. 자료구조/알고리즘 질문이 집요하게 들어옵니다.",
                                "복지가 정말 좋음. 안정적이고 배울 것이 많음. 하지만 의사결정 속도가 다소 느릴 수 있음.");

                // 3. YelloTalk (Kakao)
                createCompany("YelloTalk", "메신저 & 플랫폼", "사람과 세상을 향한 모든 연결.",
                                "안식휴가, 영어 호칭 사용, 수평적인 문화 지향",
                                "최근 잦은 조직 개편으로 인한 피로감 있음", 4.2,
                                "https://cdn-icons-png.flaticon.com/512/2111/2111728.png",
                                "Q. 메신저 시스템 설계 시 가장 중요한 고려사항은?\nQ. 동시성 문제 해결 경험은?\n\nTip: 컬처핏 면접이 매우 중요합니다. '왜 YelloTalk인가?'에 대한 진정성 있는 답변을 준비하세요.",
                                "수평적이고 자유로운 분위기. 개발자 대우가 좋음.");

                // 4. RocketDelivery (Coupang)
                createCompany("RocketDelivery", "이커머스 & 물류", "고객의 일상을 혁신합니다.",
                                "주 35시간 근무제 (월요일 오후 1시 출근), 도서 무제한 지원",
                                "성과 압박이 있음, 물류 센터 이슈 발생 시 비상 대기", 4.6,
                                "https://cdn-icons-png.flaticon.com/512/3081/3081304.png",
                                "Q. 배달 시스템에서 최단 경로 알고리즘을 어떻게 적용할 것인가?\nQ. MSA 환경에서의 트랜잭션 관리 방법은?\n\nTip: 문제 해결 능력을 중요하게 봅니다. 비즈니스 임팩트를 낸 경험을 숫자로 증명하세요.",
                                "치열하지만 보상은 확실함. 개발 문화가 잘 잡혀 있음.");

                // 5. MarketCurly (Kurly)
                createCompany("MarketCurly", "새벽 배송", "내일의 장보기, MarketCurly",
                                "퍼플 타임 (유연 근무제), 웰컴 키트 제공",
                                "초기 스타트업 분위기가 남아 있어 체계가 부족할 때가 있음", 4.0,
                                "https://cdn-icons-png.flaticon.com/512/2838/2838838.png",
                                "Q. 콜드체인 시스템을 위한 IoT 기술 활용 방안은?\nQ. 재고 관리 시스템의 동시성 이슈 해결 방안은?\n\nTip: 물류 도메인에 대한 이해도가 있으면 큰 가산점이 있습니다.",
                                "가파르게 성장하는 회사. 업무 범위가 넓어 주니어에게는 좋은 기회.");

                // --- 구직자 더미 데이터 (1년차 5명) ---
                // --- 구직자 더미 데이터 (1년차 5명) ---
                createSeeker("BackendRookie", "backend@example.com", "Backend Developer",
                                "[{\"value\":\"Naver Cloud\"},{\"value\":\"Line Plus\"}]", // 이전 직장 (JSON)
                                "Java, Spring Boot, MySQL, JPA",
                                "백엔드 시스템 설계에 관심이 많은 1년차 주니어입니다. 클린 코드와 테스트 주도 개발을 지향합니다.");

                createSeeker("FrontendNewbie", "frontend@example.com", "Frontend Developer",
                                "[{\"value\":\"Toss\"}]", // 이전 직장
                                "React, TypeScript, TailwindCSS, Recoil",
                                "사용자 경험을 최우선으로 생각하는 프론트엔드 개발자입니다. 인터랙티브한 UI 구현을 즐깁니다.");

                createSeeker("AI_Junior", "ai@example.com", "AI Engineer",
                                null, // 이전 직장 없음
                                "Python, PyTorch, TensorFlow, Pandas",
                                "데이터 분석과 딥러닝 모델링 경험이 있습니다. 최신 논문을 읽고 구현해보는 것을 좋아합니다.");

                createSeeker("DevOpsStarter", "devops@example.com", "DevOps Engineer",
                                null,
                                "Docker, Kubernetes, AWS, Jenkins",
                                "안정적인 인프라 구축과 CI/CD 파이프라인 자동화에 관심이 많습니다.");

                createSeeker("MobileJunior", "mobile@example.com", "Mobile App Developer",
                                "[{\"value\":\"Kakao Mobility\"},{\"value\":\"Daangn\"}]",
                                "Flutter, Dart, Firebase, Swift",
                                "크로스 플랫폼 앱 개발 경험이 있는 1년차 개발자입니다. 깔끔한 UI/UX 구현에 자신 있습니다.");

                System.out.println(">>> Dummy Data Initialized!");

                // [추가] 안전장치: 모든 리크루터를 확인하여 공고가 없는 리크루터에게 공고 생성
                recruiterRepository.findAll().forEach(recruiter -> {
                        if (jobPostingRepository.findByRecruiterAndStatus(recruiter, "OPEN").isEmpty()) {
                                System.out.println(">>> Creating missing job for: " + recruiter.getCompanyName());
                                createDummyJob(recruiter);
                        }
                });
        }

        private void createCompany(String name, String industry, String desc, String pros, String cons, Double rating,
                        String logo, String tips, String culture) {
                String email = name.toLowerCase() + "@example.com";

                // User 조회 혹은 생성
                User user = userRepository.findByEmail(email).orElseGet(() -> {
                        User newUser = User.builder()
                                        .email(email)
                                        .password(passwordEncoder != null ? passwordEncoder.encode("1234") : "1234")
                                        .name(name + " HR")
                                        .role(Role.RECRUITER)
                                        .intro("HR Manager at " + name)
                                        .build();
                        newUser.addPoint(10000);
                        return userRepository.save(newUser);
                });

                // Recruiter 조회 혹은 생성
                Recruiter recruiter = recruiterRepository.findByUserId(user.getId()).map(existingRecruiter -> {
                        // 이미 존재하면 정보 업데이트 (JSON 포맷 반영)
                        existingRecruiter.updateCompanyInfo(name, convertToTagJson(industry), desc, pros, cons, rating,
                                        logo);
                        existingRecruiter.updatePremiumInfo(tips, culture, "신입 초봉 5,000 ~ 6,500만원 (직무별 상이)");
                        return recruiterRepository.save(existingRecruiter);
                }).orElseGet(() -> {
                        Recruiter newRecruiter = Recruiter.builder()
                                        .user(user)
                                        .companyName(name)
                                        .industry(convertToTagJson(industry)) // [수정] JSON 변환
                                        .companyDesc(desc)
                                        .companyPros(pros)
                                        .companyCons(cons)
                                        .rating(rating)
                                        .companyLogoUrl(logo)
                                        .verifiedCode("VERIFIED")
                                        .build();
                        newRecruiter.updatePremiumInfo(tips, culture, "신입 초봉 5,000 ~ 6,500만원 (직무별 상이)");
                        return recruiterRepository.save(newRecruiter);
                });

                // [공고 생성]
                createDummyJob(recruiter);
        }

        private void createDummyJob(Recruiter recruiter) {
                // 공고 조회 (없으면 생성, 있으면 업데이트)
                List<JobPosting> existingJobs = jobPostingRepository.findByRecruiterAndStatus(recruiter, "OPEN");
                JobPosting job;

                String title = "Backend Engineer (Java/Spring)";
                String techStacks = convertToTagJson("Java, Spring Boot, JPA, MySQL, Redis");
                String requirements = convertToTagJson("Java 개발 경력 3년 이상\nSpring Framework 능숙자"); // [수정] JSON 변환
                String preferences = convertToTagJson("MSA 경험자 우대\nAWS 사용 경험자\n정보처리기사 자격증 소지자\n대용량 트래픽 처리 경험 우대"); // [수정]
                                                                                                                   // JSON
                                                                                                                   // 변환

                if (recruiter.getCompanyName().equals("GreenSpace")) {
                        title = "Search Engine Developer";
                        techStacks = convertToTagJson("C++, Java, Elasticsearch, Kafka");
                        requirements = convertToTagJson("C++ 및 Java 숙련자\n검색 엔진 이해도 보유");
                        preferences = convertToTagJson("대용량 분산 시스템 경험\n자연어 처리(NLP) 경험\n석사 이상 학위 소지자");
                } else if (recruiter.getCompanyName().equals("YelloTalk")) {
                        title = "Messaging Platform Server Developer";
                        techStacks = convertToTagJson("Kotlin, Netty, Redis, MongoDB");
                        requirements = convertToTagJson("Netty 기반 네트워크 프로그래밍 경험\nKotlin 사용 가능자");
                        preferences = convertToTagJson("대규모 메신저 개발 경험\nNoSQL 운영 경험\n오픈소스 기여 경험");
                }

                if (existingJobs.isEmpty()) {
                        job = JobPosting.builder()
                                        .recruiter(recruiter)
                                        .title(title)
                                        .description(recruiter.getCompanyName() + "에서 세상을 바꿀 개발자를 찾습니다.\n\n"
                                                        + recruiter.getCompanyDesc())
                                        .requirements(requirements)
                                        .preferences(preferences)
                                        .techStacks(techStacks)
                                        .startDate(LocalDate.now())
                                        .endDate(LocalDate.now().atTime(23, 59, 59).plusDays(30)) // [복구] 시간 추가
                                        .status("OPEN")
                                        .build();
                        jobPostingRepository.save(job);
                        System.out.println(">>> Created job for: " + recruiter.getCompanyName());
                } else {
                        // 이미 존재하면 내용 업데이트 (데이터 수정 반영을 위해)
                        job = existingJobs.get(0);
                        // update 메서드 활용 (JobPosting 엔티티에 update 메서드가 있어야 함 - 확인됨)
                        job.update(title,
                                        recruiter.getCompanyName() + "에서 세상을 바꿀 개발자를 찾습니다.\n\n"
                                                        + recruiter.getCompanyDesc(),
                                        requirements,
                                        preferences,
                                        techStacks,
                                        job.getStartDate(),
                                        LocalDate.now().atTime(23, 59, 59).plusDays(30)); // [복구] 시간 추가
                        jobPostingRepository.save(job);
                        System.out.println(">>> Updated job for: " + recruiter.getCompanyName());
                }
        }

        private void createSeeker(String nickname, String email, String position, String previousCompany,
                        String techStacks, String bio) {
                // User 조회 혹은 생성
                User user = userRepository.findByEmail(email).orElseGet(() -> {
                        User newUser = User.builder()
                                        .email(email)
                                        .password(passwordEncoder != null ? passwordEncoder.encode("1234") : "1234")
                                        .name(nickname)
                                        .role(Role.SEEKER)
                                        .intro(position + " looking for new opportunities.")
                                        .build();
                        newUser.addPoint(10000);
                        return userRepository.save(newUser);
                });

                // Seeker 조회 혹은 생성
                seekerRepository.findByUserId(user.getId()).ifPresentOrElse(existingSeeker -> {
                        // 이미 존재하면 업데이트 (JSON 포맷 반영)
                        existingSeeker.updateProfile(nickname, position, 1, previousCompany,
                                        convertToTagJson(techStacks), bio,
                                        "https://github.com/example/" + nickname);
                        seekerRepository.save(existingSeeker);
                }, () -> {
                        org.cv.moa.domain.user.entity.Seeker seeker = org.cv.moa.domain.user.entity.Seeker.builder()
                                        .user(user)
                                        .nickname(nickname)
                                        .position(position)
                                        .careerYear(1) // 1년차 통일
                                        .previousCompany(previousCompany) // [추가]
                                        .techStacks(convertToTagJson(techStacks)) // [수정] JSON 변환
                                        .bio(bio)
                                        .portfolioUrl("https://github.com/example/" + nickname)
                                        .resumeContent("안녕하세요. " + position + " 직무에서 성장하고 싶은 1년차 개발자 " + nickname
                                                        + "입니다.")
                                        .build();
                        seekerRepository.save(seeker);
                });
        }

        private String convertToTagJson(String input) {
                if (input == null || input.isEmpty())
                        return "[]";
                // 콤마(,) 또는 줄바꿈(\n)으로 분리
                String[] items = input.split("[,\\n]+");
                StringBuilder json = new StringBuilder("[");
                for (int i = 0; i < items.length; i++) {
                        String val = items[i].trim();
                        if (!val.isEmpty()) {
                                if (json.length() > 1)
                                        json.append(",");
                                json.append("{\"value\":\"").append(val).append("\"}");
                        }
                }
                json.append("]");
                return json.toString();
        }
}
