# 📄 엔티티 관계 다이어그램(ERD)
```mermaid
erDiagram
    %% 1. 공통 사용자 정보
    USERS {
        bigint user_id PK
        string email "UQ, ID"
        string password
        string role "SEEKER/RECRUITER"
    }

    %% 2. 기업 인증 코드
    COMPANY_CODES {
        string code PK
        string company_name
        boolean is_used
    }

    %% 3. 구직자 정보
    SEEKERS {
        bigint seeker_id PK
        bigint user_id FK
        string nickname
        string position
        int career_year
        text tech_stacks
        string bio
        text resume_content
        string portfolio_url
    }

    %% 4. 채용 담당자 & 기업 상세 정보 (Update: 상세 정보 필드 추가)
    RECRUITERS {
        bigint recruiter_id PK
        bigint user_id FK
        string company_name
        string company_desc
        string company_logo_url
        float rating "평점 (NEW)"
        string industry "산업 분야 (NEW)"
        text company_pros "기업 장점 (NEW)"
        text company_cons "기업 단점 (NEW)"
        string verified_code FK
    }

    %% 5. 채용 공고
    JOB_POSTINGS {
        bigint job_id PK
        bigint recruiter_id FK
        string title
        text description
        text requirements
        text tech_stacks
        string status
        datetime created_at
    }

    %% 6. 스와이프 로그
    SWIPE_LOGS {
        bigint log_id PK
        bigint actor_user_id FK
        bigint target_id
        string target_type "JOB/SEEKER"
        string action "LIKE/DISLIKE"
        datetime created_at
    }

    %% 7. 매칭 및 채팅
    MATCHES {
        bigint match_id PK
        bigint seeker_id FK
        bigint job_id FK
        datetime matched_at
    }

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

    %% 관계선
    USERS ||--o| SEEKERS : "details"
    USERS ||--o| RECRUITERS : "details"
    
    RECRUITERS ||--o{ JOB_POSTINGS : "posts"
    COMPANY_CODES ||--o| RECRUITERS : "verifies"
    
    USERS ||--o{ SWIPE_LOGS : "logs"
    
    SEEKERS ||--o{ MATCHES : "matched"
    JOB_POSTINGS ||--o{ MATCHES : "matched"
    
    MATCHES ||--|{ CHAT_ROOMS : "opens"
    CHAT_ROOMS ||--o{ CHAT_MESSAGES : "contains"
