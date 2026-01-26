package org.cv.moa.domain.chat.dto; // 이 줄이 파일 맨 위에 있어야 함

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponse {
    private Long msgId;
    private String senderName;
    private String message;
    private LocalDateTime sentAt;
    private boolean isMe;
}