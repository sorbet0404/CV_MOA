package org.cv.moa.domain.chat.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.cv.moa.domain.chat.dto.ChatMessageRequest;
import org.cv.moa.domain.chat.entity.ChatRoom;
import org.cv.moa.domain.chat.service.ChatService;
import org.cv.moa.domain.chat.repository.ChatRoomRepository; // 기존 유지 (리팩토링 최소화)
import org.cv.moa.domain.user.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.time.LocalDate;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatService chatService;

    // 채팅방 입장 (상세 화면)
    @GetMapping("/room/{roomId}")
    public String enterRoom(@PathVariable Long roomId, Model model) {
        ChatRoom currentRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 방입니다."));

        // 햄버거 메뉴용 다른 방 목록
        List<ChatRoom> allRooms = chatRoomRepository.findAll();
        List<ChatRoom> otherRooms = allRooms.stream()
                .filter(room -> !room.getId().equals(roomId))
                .collect(Collectors.toList());

        model.addAttribute("room", currentRoom);
        model.addAttribute("otherRooms", otherRooms);

        // [추가] 이전 대화 내용 불러오기
        model.addAttribute("messages", chatService.getMessages(roomId));

        // [추가] 공고 마감 여부 확인
        boolean isClosed = false;
        try {
            org.cv.moa.domain.job.entity.JobPosting job = currentRoom.getMatch().getJobPosting();
            if (job.getStatus().toString().equals("CLOSED") ||
                    (job.getEndDate() != null && job.getEndDate().isBefore(java.time.LocalDateTime.now()))) {
                isClosed = true;
            }
        } catch (Exception e) {
            // 매칭 정보가 없거나 에러 발생 시 안전하게 처리
            e.printStackTrace();
        }
        model.addAttribute("isClosed", isClosed);

        return "chat/room";
    }

    // 메시지 저장 (AJAX 요청)
    @PostMapping("/room/{roomId}/message")
    @ResponseBody
    public ResponseEntity<String> saveMessage(@PathVariable Long roomId, @RequestBody ChatMessageRequest request,
            HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        chatService.saveMessage(roomId, user.getId(), request.getMessage());
        return ResponseEntity.ok("전송 완료");
    }

    // [문제 9번 해결] 채팅방 나가기 (삭제)
    @PostMapping("/room/{roomId}/leave")
    public String leaveRoom(@PathVariable Long roomId, HttpSession session) {
        chatService.deleteRoom(roomId);

        User user = (User) session.getAttribute("user");
        if (user != null && user.getRole().name().equals("RECRUITER")) {
            return "redirect:/matching/recruiter";
        }
        return "redirect:/matching";
    }
}
