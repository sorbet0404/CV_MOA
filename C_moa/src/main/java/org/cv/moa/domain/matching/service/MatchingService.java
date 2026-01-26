package org.cv.moa.domain.matching.service;

import lombok.RequiredArgsConstructor;
import org.cv.moa.domain.chat.entity.ChatRoom;
import org.cv.moa.domain.chat.entity.Match;
import org.cv.moa.domain.chat.repository.ChatRoomRepository;
import org.cv.moa.domain.chat.repository.MatchRepository;
import org.cv.moa.domain.job.entity.JobPosting;
import org.cv.moa.domain.job.repository.JobPostingRepository;
import org.cv.moa.domain.matching.entity.Proposal;
import org.cv.moa.domain.matching.entity.ProposalStatus;
import org.cv.moa.domain.matching.entity.ProposalType;
import org.cv.moa.domain.matching.repository.ProposalRepository;
import org.cv.moa.domain.user.entity.Recruiter;
import org.cv.moa.domain.user.entity.Role;
import org.cv.moa.domain.user.entity.Seeker;
import org.cv.moa.domain.user.entity.User;
import org.cv.moa.domain.user.repository.RecruiterRepository;
import org.cv.moa.domain.user.repository.SeekerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatchingService {

    private final ProposalRepository proposalRepository;
    private final MatchRepository matchRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final JobPostingRepository jobPostingRepository;
    private final SeekerRepository seekerRepository;
    private final RecruiterRepository recruiterRepository;

    /**
     * 제안 보내기 (좋아요/싫어요/슈퍼라이크)
     */
    @Transactional
    public String propose(User user, Long targetId, boolean isSuperLike) {
        // 기존 propose와 동일하게 처리 (isLike=true 전제)
        if (user.getRole() == Role.SEEKER) {
            return handleSeekerProposal(user, targetId, true, isSuperLike);
        } else {
            return handleRecruiterProposal(user, targetId, null, true, isSuperLike);
        }
    }

    // [추가] 기존 Controller 호환용
    @Transactional
    public String propose(User user, Long targetId, Long jobId, boolean isLike) {
        if (user.getRole() == Role.SEEKER) {
            return handleSeekerProposal(user, targetId, isLike, false); // 슈퍼라이크 기본값 false
        } else {
            return handleRecruiterProposal(user, targetId, jobId, isLike, false);
        }
    }

    // 싫어요 처리를 위한 오버로딩 (Controller가 지원한다면)
    @Transactional
    public String propose(User user, Long targetId, boolean isLike, boolean isSuperLike) {
        if (user.getRole() == Role.SEEKER) {
            return handleSeekerProposal(user, targetId, isLike, isSuperLike);
        } else {
            return handleRecruiterProposal(user, targetId, null, isLike, isSuperLike);
        }
    }

    private String handleSeekerProposal(User user, Long jobId, boolean isLike, boolean isSuperLike) {
        Seeker seeker = seekerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("구직자 정보 없음"));
        JobPosting jobPosting = jobPostingRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("채용 공고 없음"));

        // 슈퍼라이크 처리 (포인트 차감) - 좋아요일 때만
        if (isLike && isSuperLike) {
            // [Fix] 영속성 컨텍스트 관리하에 있는 User 객체 사용 (DB 반영을 위해)
            User managedUser = seeker.getUser();
            managedUser.deductPoint(100);
        }

        return processProposal(seeker, jobPosting, ProposalType.APPLY, isLike, isSuperLike);
    }

    private String handleRecruiterProposal(User user, Long seekerId, Long jobId, boolean isLike, boolean isSuperLike) {
        Recruiter recruiter = recruiterRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("기업 회원 정보 없음"));
        Seeker seeker = seekerRepository.findById(seekerId)
                .orElseThrow(() -> new IllegalArgumentException("구직자 정보 없음"));

        JobPosting jobPosting;
        if (jobId != null) {
            jobPosting = jobPostingRepository.findById(jobId)
                    .orElseThrow(() -> new IllegalArgumentException("채용 공고 없음"));
        } else {
            jobPosting = jobPostingRepository.findByRecruiterAndStatus(recruiter, "OPEN").stream()
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("진행 중인 채용 공고가 없습니다. 공고를 먼저 등록해주세요."));
        }

        // [Fix] 리크루터 슈퍼라이크 포인트 차감
        if (isLike && isSuperLike) {
            User managedUser = recruiter.getUser();
            managedUser.deductPoint(100);
        }

        return processProposal(seeker, jobPosting, ProposalType.SCOUT, isLike, isSuperLike); // isSuperLike 전달
    }

    private String processProposal(Seeker seeker, JobPosting jobPosting, ProposalType currentType, boolean isLike,
            boolean isSuperLike) {
        // 싫어요(PASS)인 경우: REJECTED로 기록 (매칭 시도 X)
        if (!isLike) {
            Proposal rejectProposal = Proposal.builder()
                    .seeker(seeker)
                    .jobPosting(jobPosting)
                    .type(currentType)
                    .status(ProposalStatus.REJECTED)
                    .isSuperLike(false)
                    .build();
            proposalRepository.save(rejectProposal);
            return "PASSED";
        }

        Optional<Proposal> existingProposal = proposalRepository.findBySeekerAndJobPosting(seeker, jobPosting);

        if (existingProposal.isPresent()) {
            Proposal proposal = existingProposal.get();

            // 이미 매칭된 경우
            if (proposal.getStatus() == ProposalStatus.ACCEPTED) {
                return "ALREADY_MATCHED";
            }

            // 내가 보낸게 아니고, 상대방이 보낸(WAITING) 제안이 있다면 -> 매칭 성사!
            if (proposal.getType() != currentType && proposal.getStatus() == ProposalStatus.WAITING) {
                proposal.accept(); // 상태 ACCEPTED로 변경
                createMatchAndChatRoom(seeker, jobPosting);
                return "MATCHED";
            }

            // 이미 내가 보낸 경우
            if (proposal.getType() == currentType) {
                return "ALREADY_SENT";
            }
        } else {
            // 제안 없음 -> 새로 생성 (WAITING)
            Proposal newProposal = Proposal.builder()
                    .seeker(seeker)
                    .jobPosting(jobPosting)
                    .type(currentType)
                    .status(ProposalStatus.WAITING)
                    .isSuperLike(isSuperLike)
                    .build();
            proposalRepository.save(newProposal);
            return "SENT";
        }

        return "UNKNOWN";
    }

    private void checkMatch(Seeker seeker, JobPosting jobPosting) {
        // processProposal 내부에서 이미 처리하므로 별도 checkMatch 메소드는 사실상 processProposal 로직으로
        // 흡수됨.
        // 하지만 기존 코드와의 호환성을 위해 남겨두거나 삭제 가능.
        // 여기서는 삭제하고 processProposal에서직접 처리.
    }

    private void createMatchAndChatRoom(Seeker seeker, JobPosting jobPosting) {
        // 1. 매칭 정보 저장
        Match match = new Match(seeker, jobPosting);
        match = matchRepository.save(match);

        // 2. 채팅방 생성
        ChatRoom chatRoom = ChatRoom.builder()
                .match(match)
                .build();
        chatRoomRepository.save(chatRoom);
    }

    /**
     * 구직자용: 탐색할 채용 공고 목록 (이미 제안한 공고 제외)
     */
    public List<JobPosting> getJobPostingsToSwipe(User user) {
        Seeker seeker = seekerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("구직자 정보 없음"));

        List<Long> interactedJobIds = proposalRepository.findInteractedJobIds(seeker.getId());

        // 전체 OPEN 공고 중 제외
        List<JobPosting> allOpenJobs = jobPostingRepository.findByStatus("OPEN");

        if (interactedJobIds.isEmpty())
            return allOpenJobs;

        return allOpenJobs.stream()
                .filter(job -> !interactedJobIds.contains(job.getId()))
                .collect(Collectors.toList());
    }

    /**
     * 기업용: 탐색할 구직자 목록 (이미 제안한 사람 제외)
     */
    public List<Seeker> getSeekersToSwipe(User user) {
        Recruiter recruiter = recruiterRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("기업 정보 없음"));

        List<Long> interacteIds = proposalRepository.findInteractedSeekerIds(recruiter.getId());

        // 전체 구직자 중 제외
        List<Seeker> allSeekers = seekerRepository.findAll();

        if (interacteIds.isEmpty())
            return allSeekers;

        return allSeekers.stream()
                .filter(s -> !interacteIds.contains(s.getId()))
                .collect(Collectors.toList());
    }

    public List<Proposal> getProposalsForSeeker(User user) {
        Seeker seeker = seekerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("구직자 정보 없음"));
        return proposalRepository.findBySeekerAndTypeAndStatusOrderByIsSuperLikeDescCreatedAtDesc(seeker,
                ProposalType.SCOUT, ProposalStatus.WAITING);
    }

    public List<Proposal> getProposalsForRecruiter(User user) {
        Recruiter recruiter = recruiterRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("기업 정보 없음"));
        return proposalRepository.findRecruiterProposals(recruiter.getId(), ProposalType.APPLY, ProposalStatus.WAITING);
    }

    public List<ChatRoom> getChatRooms(User user) {
        if (user.getRole() == Role.SEEKER) {
            Seeker seeker = seekerRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new IllegalArgumentException("구직자 정보 없음"));
            return chatRoomRepository.findBySeekerId(seeker.getId());
        } else {
            Recruiter recruiter = recruiterRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new IllegalArgumentException("기업 정보 없음"));
            return chatRoomRepository.findByRecruiterId(recruiter.getId());
        }
    }
}
