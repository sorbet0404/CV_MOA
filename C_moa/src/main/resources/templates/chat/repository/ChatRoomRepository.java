package org.cv.moa.domain.chat.repository;

import org.cv.moa.domain.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    // 1. 구직자(Seeker)용: 내 ID(seekerId)가 포함된 매칭의 채팅방 찾기
    @Query("SELECT c FROM ChatRoom c JOIN FETCH c.match m JOIN FETCH m.jobPosting j JOIN FETCH j.recruiter r " +
            "WHERE m.seeker.seekerId = :seekerId")
    List<ChatRoom> findBySeekerId(@Param("seekerId") Long seekerId);

    // 2. 채용 담당자(Recruiter)용: 내가 올린 공고(jobPosting)에 연결된 채팅방 찾기
    @Query("SELECT c FROM ChatRoom c JOIN FETCH c.match m JOIN FETCH m.seeker s " +
            "WHERE m.jobPosting.recruiter.recruiterId = :recruiterId")
    List<ChatRoom> findByRecruiterId(@Param("recruiterId") Long recruiterId);
}