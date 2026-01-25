# 📄 엔티티 관계 다이어그램(ERD)
```mermaid
erDiagram
    %% 1. 사용자 및 인증 (포인트 시스템 추가됨)
    USERS {
        bigint user_id PK
        string email
        string password
        string role
        int point "[NEW] 게임화 요소 (잠금해제/슈퍼라이크 사용)"
        datetime created_at
        datetime last_login_at
    }

    %% 2. 구직자 상세 정보 (이전 직장 추가됨)
    SEEKER {
        bigint profile_id PK
        bigint user_id FK
        string nickname
        string position
        int career_year
        string previous_company "[NEW] 이전 직장 (경력직용)"
        text tech_stacks
        string bio
        text resume_content
        string portfolio_url
    }

    %% 3. 채용 담당자 및 상세 정보 (프리미엄 컨텐츠 대거 추가됨)
    RECRUITER {
        bigint profile_id PK
        bigint user_id FK
        string company_name
        string company_desc
        string company_logo_url
        string verified_code
        double rating "[NEW] 기업 평점"
        string industry "[NEW] 산업군"
        text company_pros "[NEW] 기업 장점"
        text company_cons "[NEW] 기업 단점"
        text interview_tips "[NEW] 면접 꿀팁 (50P 잠금해제)"
        text company_culture "[NEW] 조직 문화 (50P 잠금해제)"
        string salary_info "[NEW] 연봉 정보 (50P 잠금해제)"
    }

    JOB_POSTINGS {
        bigint job_id PK
        bigint recruiter_id FK
        string title
        text description
        text requirements
        text preferences "[MODIFIED] 우대사항 필드 명시"
        text tech_stacks
        string status
        datetime created_at
        datetime end_date "[MODIFIED] 마감일 관리"
    }

    %% 4. 매칭 시스템 (로그 방식 -> 제안(Proposal) 방식으로 변경됨)
    PROPOSALS {
        bigint proposal_id PK
        bigint seeker_id FK
        bigint job_id FK
        string type "APPLY(지원) or SCOUT(제안)"
        string status "WAITING / ACCEPTED / REJECTED"
        boolean is_super_like "[NEW] 슈퍼라이크 여부 (100P 소모)"
        datetime created_at
    }

    %% 매칭 성사 테이블 (변경 없음)
    MATCHES {
        bigint match_id PK
        bigint seeker_id FK
        bigint job_id FK
        datetime matched_at
    }

    %% 5. 채팅
    CHAT_ROOMS {
        bigint room_id PK
        bigint match_id FK
    }

    CHAT_MESSAGES {
        bigint msg_id PK
        bigint room_id FK
        bigint sender_id FK
        text message
        boolean is_read
        datetime sent_at
    }

    %% 관계 정의
    USERS ||--o| SEEKER : "has"
    USERS ||--o| RECRUITER : "has"
    RECRUITER ||--o{ JOB_POSTINGS : "posts"

    %% 변경된 매칭 흐름: 로그 대신 Proposal이 핵심
    SEEKER ||--o{ PROPOSALS : "sends/receives"
    JOB_POSTINGS ||--o{ PROPOSALS : "sends/receives"

    SEEKER ||--o{ MATCHES : "matches"
    JOB_POSTINGS ||--o{ MATCHES : "matches"
    
    MATCHES ||--|{ CHAT_ROOMS : "creates"
    CHAT_ROOMS ||--o{ CHAT_MESSAGES : "contains"
