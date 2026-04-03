# 🤝 Cmoa (Project Name CV_MOA)
> **Swipe Right for Your Dream Job!**
> 구직자와 채용 담당자를 틴더(Tinder) 방식의 UI로 연결하는 신개념 매칭 플랫폼

**Cmoa**는 딱딱하고 지루한 기존의 채용 프로세스를 벗어나, **직관적인 스와이프(Swipe)** 방식을 통해 구직자와 기업을 연결하는 서비스입니다.

구직자는 기업의 핵심 가치를, 기업은 인재의 포트폴리오를 카드 형태로 확인하며 빠르고 간편하게 서로의 니즈(Needs)를 파악할 수 있습니다. 양쪽이 모두 'Like'를 보냈을 때 비로소 매칭이 성사되어 대화를 시작할 수 있습니다.

![Swiper UI Preview](https://via.placeholder.com/800x400.png?text=C-MOA+Swipe+UI+Preview)
*(여기에 실제 스크린샷을 넣으세요)*

### ✨ 주요 기능 (Key Features)
* **Swipe UI**: 좌우 스와이프를 통해 채용 공고/인재 프로필을 간편하게 탐색 (CSS 3D Animation 적용)
* **Super Like**: 100 포인트를 소모하여 상대방에게 강력한 호감을 표시하고, 매칭 확률을 높임
* **Real-time Matching**: 상호 호감이 확인되는 즉시 매칭 알림 전송 및 채팅방 생성
* **Chatting**: 매칭된 구직자와 채용담당자 간의 STOMP 기반 실시간 1:1 대화
* **Premium Insight**: 50 포인트를 사용하여 기업의 면접 꿀팁, 연봉 정보, 조직 문화 등 시크릿 정보 잠금 해제
* **Resume/Portfolio**: 간편하게 등록하고 시각적으로 돋보이는 프로필 카드

<br/>

## 🛠 기술 스택 (Tech Stack)

| 구분 | 기술 (Technology) |
| :-- | :-- |
| **Language** | ![Java](https://img.shields.io/badge/Java-17-007396?style=flat-square&logo=java&logoColor=white) |
| **Frontend** | ![HTML5](https://img.shields.io/badge/HTML5-E34F26?style=flat-square&logo=html5&logoColor=white) ![CSS3](https://img.shields.io/badge/CSS3-1572B6?style=flat-square&logo=css3&logoColor=white) ![JavaScript](https://img.shields.io/badge/JavaScript-F7DF1E?style=flat-square&logo=javascript&logoColor=black) ![Thymeleaf](https://img.shields.io/badge/Thymeleaf-005F0F?style=flat-square&logo=thymeleaf&logoColor=white) ![Bootstrap](https://img.shields.io/badge/Bootstrap_5-7952B3?style=flat-square&logo=bootstrap&logoColor=white) |
| **Backend** | ![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2-6DB33F?style=flat-square&logo=springboot&logoColor=white) ![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=flat-square&logo=springsecurity&logoColor=white) ![Spring Data JPA](https://img.shields.io/badge/Spring_Data_JPA-6DB33F?style=flat-square&logo=spring&logoColor=white) ![WebSocket](https://img.shields.io/badge/WebSocket_(STOMP)-010101?style=flat-square&logo=socket.io&logoColor=white) |
| **Database** | ![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=flat-square&logo=mysql&logoColor=white) |
| **Build Tool** | ![Gradle](https://img.shields.io/badge/Gradle-02303A?style=flat-square&logo=gradle&logoColor=white) |
| **Server** | ![Tomcat](https://img.shields.io/badge/Apache_Tomcat-F8DC75?style=flat-square&logo=apachetomcat&logoColor=black) |

<br/>

## 🏗 시스템 아키텍처 (System Architecture)

```mermaid
graph TD
    %% 스타일 정의 (Brand Colors)
    classDef client fill:#f9f9f9,stroke:#333,stroke-width:2px;
    classDef spring fill:#6DB33F,stroke:#333,stroke-width:2px,color:white;
    classDef db fill:#4479A1,stroke:#333,stroke-width:2px,color:white;
    classDef infra fill:#e1e1e1,stroke:#333,stroke-width:2px,stroke-dasharray: 5 5;

    %% 1. Client Side
    subgraph Client [📱 Client Side]
        User((User))
        Browser[Mobile/PC Browser]
        User --> Browser
    end

    %% 2. Server Side
    subgraph Server [🍃 Spring Boot Server]
        direction TB
        Security[Spring Security<br/>Auth/Session]
        Controller[Controller Layer<br/>Page/API Routing]
        Service[Service Layer<br/>Business Logic]
        WebSocket[WebSocket<br/>STOMP Chat]
    end

    %% 3. Data Layer
    subgraph Data [💾 Data Infrastructure]
        DB[(MySQL Database)]
        FileSys[File System<br/>Uploads]
    end

    %% Flow Connections
    Browser -- "HTTP/HTTPS" --> Security
    Security --> Controller
    Controller --> Service
    
    Browser <== "WS/WSS" ==> WebSocket
    WebSocket <--> Service
    
    Service -- "JPA/Hibernate" --> DB
    Service -- "I/O" --> FileSys
    
    %% Return Path (Thymeleaf SSR)
    Controller -.->|Thymeleaf View| Browser

    %% 클래스 적용
    class User,Browser client;
    class Security,Controller,Service,WebSocket spring;
    class DB db;
    class FileSys infra;
```

더 자세한 기획 의도와 요구사항 명세는 아래 문서를 참고해 주세요.



👉 **[소프트웨어 요구사항 명세서 (SRS) 보러 가기](https://github.com/sorbet0404/CV_MOA/blob/docs/C_MOA_SRS.md)**

<br/>
