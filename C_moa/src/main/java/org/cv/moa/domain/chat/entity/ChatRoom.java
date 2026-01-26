package org.cv.moa.domain.chat.entity;

import jakarta.persistence.*;
import lombok.*;
import org.cv.moa.domain.chat.entity.ChatMessage; // ChatMessage 위치 확인
import org.cv.moa.domain.chat.entity.Match;        // Match 위치 확인

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter // ★ 필수: 이게 있어야 room.getId()를 쓸 수 있습니다!
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "chat_rooms") // ERD 테이블명 일치
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id") // ★ ERD의 room_id와 매핑
    private Long id;          // 자바에서는 편하게 'id'라고 부름 (getId()로 호출)

    // ERD의 match_id (FK)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id")
    private Match match;

    // ★ ERD의 CHAT_MESSAGES 테이블과 연결 (줄줄이 사탕)
    // "이 방(chatRoom)이랑 연결된 메시지들을 리스트로 가져와라"
    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
    @Builder.Default
    private List<ChatMessage> messages = new ArrayList<>();
}