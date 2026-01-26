package org.cv.moa.domain.chat.repository;

import org.cv.moa.domain.chat.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // [수정 전] findByChatRoom_RoomIdOrderBySentAtAsc (에러 원인: roomId라는 변수 없음)
    // [수정 후] findByChatRoomIdOrderBySentAtAsc (정답: ChatRoom의 id 변수를 찾음)

    // 특정 채팅방의 메시지를 시간순으로 가져오기
    List<ChatMessage> findByChatRoomIdOrderBySentAtAsc(Long roomId);
}