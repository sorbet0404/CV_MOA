package org.cv.moa.domain.chat.service;

import lombok.RequiredArgsConstructor;
import org.cv.moa.domain.chat.entity.ChatMessage;
import org.cv.moa.domain.chat.entity.ChatRoom;
import org.cv.moa.domain.chat.repository.ChatMessageRepository;
import org.cv.moa.domain.chat.repository.ChatRoomRepository;
import org.cv.moa.domain.user.entity.User;
import org.cv.moa.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    @Transactional
    public ChatMessage saveMessage(Long roomId, Long senderId, String content) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));

        // [추가] 채용 공고 마감 여부 확인
        if (room.getMatch() != null && room.getMatch().getJobPosting() != null) {
            String status = room.getMatch().getJobPosting().getStatus();
            if ("CLOSED".equals(status)) {
                throw new IllegalArgumentException("채용이 마감된 공고입니다. 메시지를 보낼 수 없습니다.");
            }
        }

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));

        ChatMessage message = new ChatMessage(room, sender, content);
        return chatMessageRepository.save(message);
    }

    public List<ChatMessage> getMessages(Long roomId) {
        return chatMessageRepository.findByChatRoomIdOrderBySentAtAsc(roomId);
    }

    @Transactional
    public void deleteRoom(Long roomId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));

        org.cv.moa.domain.chat.entity.Match match = room.getMatch();

        // 1. Proposal 삭제 (매칭 이력 삭제하여 다시 스와이프 가능하게)
        if (match != null) {
            org.cv.moa.domain.matching.repository.ProposalRepository proposalRepository = applicationContext
                    .getBean(org.cv.moa.domain.matching.repository.ProposalRepository.class);
            proposalRepository.findBySeekerAndJobPosting(match.getSeeker(), match.getJobPosting())
                    .ifPresent(proposalRepository::delete);

            // 2. ChatRoom 삭제
            chatRoomRepository.delete(room);

            // 3. Match 삭제
            org.cv.moa.domain.chat.repository.MatchRepository matchRepository = applicationContext
                    .getBean(org.cv.moa.domain.chat.repository.MatchRepository.class);
            matchRepository.delete(match);
        } else {
            chatRoomRepository.delete(room);
        }
    }

    @org.springframework.beans.factory.annotation.Autowired
    private org.springframework.context.ApplicationContext applicationContext;
}
