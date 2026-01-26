package org.cv.moa.domain.chat.repository;

import org.cv.moa.domain.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

        java.util.Optional<ChatRoom> findByMatch(org.cv.moa.domain.chat.entity.Match match);

        // 1. 구직자(Seeker)용
        // m.seeker.seekerId (X) -> m.seeker.id (O)
        @Query("SELECT c FROM ChatRoom c JOIN FETCH c.match m JOIN FETCH m.jobPosting j JOIN FETCH j.recruiter r " +
                        "WHERE m.seeker.id = :seekerId")
        List<ChatRoom> findBySeekerId(@Param("seekerId") Long seekerId);

        // 2. 채용 담당자(Recruiter)용
        // m.jobPosting.recruiter.recruiterId (X) -> m.jobPosting.recruiter.id (O)
        @Query("SELECT c FROM ChatRoom c JOIN FETCH c.match m JOIN FETCH m.seeker s " +
                        "WHERE m.jobPosting.recruiter.id = :recruiterId")
        List<ChatRoom> findByRecruiterId(@Param("recruiterId") Long recruiterId);
}