package com.CodeLab.DB_Service.service;

import com.CodeLab.DB_Service.exceptions.NotFoundException;
import com.CodeLab.DB_Service.model.*;
import com.CodeLab.DB_Service.repository.*;
import com.CodeLab.DB_Service.requestDTO.ContestRequestDTO;
import com.CodeLab.DB_Service.requestDTO.PartialContestSubmissionRequestDTO;
import com.CodeLab.DB_Service.requestDTO.ProblemRequestDTO;
import com.CodeLab.DB_Service.requestDTO.UpdatePartialContestSubmissionRequestDTO;
import com.CodeLab.DB_Service.requestdto_converter.ContestConverter;
import com.CodeLab.DB_Service.requestdto_converter.PartialContestSubmissionConverter;
import com.CodeLab.DB_Service.responseDTO.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;


import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ContestService {

    @Autowired
    ContestRepo contestRepo;

    @Autowired
    ContestProblemRepo contestProblemRepo;

    @Autowired
    ProblemService problemService;

    @Autowired
    ContestUserRepo contestUserRepo;

    @Autowired
    UserService userService;

    @Autowired
    FullContestSubmissionRepo submissionRepo;

    @Autowired
    PartialContestSubmissionRepo partialContestSubmissionRepo;

    @Autowired
    LeaderboardEntryRepo leaderboardEntryRepo;

    public ContestAddedResponseDTO addContest(ContestRequestDTO requestDTO) {
        Contest contest = ContestConverter.contestConverter(requestDTO);
        contest = contestRepo.save(contest);

        List<ProblemRequestDTO> newProblemList = requestDTO.getNewProblemList();
        List<UUID> existingProblemList = requestDTO.getExistingProblemList();

        List<Problem> problemList = new ArrayList<>();

        // Add existing problems
        for (UUID id : existingProblemList) {
            Problem problem = problemService.getProblem(id);
            problemList.add(problem);
        }

        // Add new problems
        for (ProblemRequestDTO problemRequestDTO : newProblemList) {
            Problem problem = problemService.addProblem(problemRequestDTO, false);
            problemList.add(problem);
        }

        // ✅ Sort by Difficulty: EASY (0), MEDIUM (1), HARD (2)
        problemList.sort(Comparator.comparing(p -> p.getProblemDifficulty().ordinal()));

        // Assign contest question numbers based on sorted order
        List<ContestProblem> contestProblemList = new ArrayList<>();
        int questionNo = 1;
        for (Problem problem : problemList) {
            ContestProblem contestProblem = new ContestProblem();
            contestProblem.setProblem(problem);
            contestProblem.setContest(contest);
            contestProblem.setContestQuestionNo(questionNo++);

            contestProblem = contestProblemRepo.save(contestProblem);
            contestProblemList.add(contestProblem);
        }

        // Prepare response
        ContestAddedResponseDTO responseDTO = new ContestAddedResponseDTO();
        responseDTO.setContest(contest);
        responseDTO.setContestProblemList(contestProblemList);

        return responseDTO;
    }


    public Contest getContestById(UUID contestId) {
        return contestRepo.findById(contestId).orElse(null);
    }

    public List<UpcomingContestResponseDTO> getUpcomingContests(UUID userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            throw new NotFoundException("User with id-" + userId + " not Found!!!");
        }

        List<Contest> list = contestRepo.findAllUpcomingContests();

        List<UpcomingContestResponseDTO> responseDTOList = new ArrayList<>();

        for (Contest contest : list) {
            ContestUser contestUser = contestUserRepo.findByUserAndContest(userId, contest.getContestId()).orElse(null);
            UpcomingContestResponseDTO responseDTO = new UpcomingContestResponseDTO();
            responseDTO.setContest(contest);
            if (contestUser != null) {
                responseDTO.setUserAlreadyRegistered(true);
            }
            responseDTOList.add(responseDTO);
        }
        return responseDTOList;
    }

    public List<UpcomingContestResponseDTO> getUpcomingContests(int pageNo, UUID userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            throw new NotFoundException("User with id-" + userId + " not Found!!!");
        }

        int pageSize = 10;
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        List<Contest> list = contestRepo.findAllUpcomingContests(pageable).getContent();

        List<UpcomingContestResponseDTO> responseDTOList = new ArrayList<>();

        for (Contest contest : list) {
            ContestUser contestUser = contestUserRepo.findByUserAndContest(userId, contest.getContestId()).orElse(null);
            UpcomingContestResponseDTO responseDTO = new UpcomingContestResponseDTO();
            responseDTO.setContest(contest);
            if (contestUser != null) {
                responseDTO.setUserAlreadyRegistered(true);
            }
            responseDTOList.add(responseDTO);
        }
        return responseDTOList;
    }

    public ContestUser registerForContest(UUID userId, UUID contestId) {
        // 1. Check contest existence
        Contest contest = contestRepo.findById(contestId).orElse(null);
        if (contest == null) {
            throw new NotFoundException("Contest with id-" + contestId + " not Found!!!");
        }

        // 2. Check if contest has started
        LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault());
        if (contest.getStartTime().isBefore(now)) {
            throw new RuntimeException("Registration closed. Contest has already started.");
        }

        // 3. Check user existence
        User user = userService.getUserById(userId);
        if (user == null) {
            throw new NotFoundException("User with id-" + userId + " not Found!!!");
        }

        // 4. Check if user is already registered
        Optional<ContestUser> existing = contestUserRepo.findByUserAndContest(userId, contestId);
        if (existing.isPresent()) {
            throw new RuntimeException("User is already registered for this contest.");
        }

        // 5. Register user for contest
        ContestUser contestUser = new ContestUser();
        contestUser.setUser(user);
        contestUser.setContest(contest);
        contestUserRepo.save(contestUser);

        return contestUser;
    }

    public List<LiveContestResponseDTO> getLiveContests(UUID userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            throw new NotFoundException("User with id-" + userId + " not Found!!!");
        }

        LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault());
        List<Contest> liveContests = contestRepo.findAllLiveContests(now);
        List<LiveContestResponseDTO> responseList = new ArrayList<>();

        for (Contest contest : liveContests) {
            UUID contestId = contest.getContestId();
            long durationSeconds = contest.getDuration();

            boolean isRegistered = contestUserRepo
                    .findByUserAndContest(userId, contestId)
                    .isPresent();

            Optional<FullContestSubmission> submissionOpt =
                    submissionRepo.findByContestIdAndUserId(contestId, userId);

            boolean isSubmitted = false;
            boolean isRejoin = false;
            long remainingTimeInSeconds = durationSeconds;

            if (submissionOpt.isPresent()) {
                FullContestSubmission submission = submissionOpt.get();

                if (submission.getUserSubmittedAt() != null) {
                    isSubmitted = true;
                } else {
                    isRejoin = true;
                    LocalDateTime userDeadline = submission.getUserStartedAt().plusSeconds(durationSeconds);
                    LocalDateTime effectiveDeadline = userDeadline.isBefore(contest.getEndTime())
                            ? userDeadline
                            : contest.getEndTime();

                    remainingTimeInSeconds = Duration.between(now, effectiveDeadline).getSeconds();
                    remainingTimeInSeconds = Math.max(remainingTimeInSeconds, 0);
                }
            }

            LiveContestResponseDTO dto = new LiveContestResponseDTO();
            dto.setContest(contest);
            dto.setUserRegistered(isRegistered);
            dto.setContestSubmitted(isSubmitted);
            dto.setUserRejoin(isRejoin);
            dto.setRemainingTimeInSeconds(remainingTimeInSeconds);

            responseList.add(dto);
        }

        return responseList;
    }


    public List<LiveContestResponseDTO> getLiveContests(UUID userId, int pageNo) {
        int pageSize = 2;

        User user = userService.getUserById(userId);
        if (user == null) {
            throw new NotFoundException("User with id-" + userId + " not Found!!!");
        }

        LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault());
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);

        List<Contest> contestList = contestRepo.findAllLiveContestsPaginated(now, pageable).getContent();
        List<LiveContestResponseDTO> responseDTOList = new ArrayList<>();

        for (Contest contest : contestList) {
            UUID contestId = contest.getContestId();
            long durationSeconds = contest.getDuration();

            boolean isRegistered = contestUserRepo
                    .findByUserAndContest(userId, contestId)
                    .isPresent();

            Optional<FullContestSubmission> submissionOpt =
                    submissionRepo.findByContestIdAndUserId(contestId, userId);

            boolean isSubmitted = false;
            boolean isRejoin = false;
            long remainingTimeInSeconds = durationSeconds;

            if (submissionOpt.isPresent()) {
                FullContestSubmission submission = submissionOpt.get();

                if (submission.getUserSubmittedAt() != null) {
                    isSubmitted = true;
                } else {
                    isRejoin = true;
                    LocalDateTime userDeadline = submission.getUserStartedAt().plusSeconds(durationSeconds);
                    LocalDateTime effectiveDeadline = userDeadline.isBefore(contest.getEndTime())
                            ? userDeadline
                            : contest.getEndTime();

                    remainingTimeInSeconds = Duration.between(now, effectiveDeadline).getSeconds();
                    remainingTimeInSeconds = Math.max(remainingTimeInSeconds, 0);
                }
            }

            LiveContestResponseDTO dto = new LiveContestResponseDTO();
            dto.setContest(contest);
            dto.setUserRegistered(isRegistered);
            dto.setContestSubmitted(isSubmitted);
            dto.setUserRejoin(isRejoin);
            dto.setRemainingTimeInSeconds(remainingTimeInSeconds);

            responseDTOList.add(dto);
        }

        return responseDTOList;
    }


//    public static String formatDuration(long totalSeconds) {
//        long hours = totalSeconds / 3600;
//        long minutes = (totalSeconds % 3600) / 60;
//        long seconds = totalSeconds % 60;
//
//        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
//    }

    public ContestStartResponseDTO userStartsContest(UUID userId, UUID contestId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            throw new NotFoundException("User with id-" + userId + " not Found!!!");
        }

        Contest contest = contestRepo.findById(contestId).orElse(null);
        if (contest == null) {
            throw new NotFoundException("Contest with id-" + contestId + " not Found!!!");
        }

        if (LocalDateTime.now().isBefore(contest.getStartTime())) {
            throw new RuntimeException("Contest with id-" + contestId + " has not started yet!!!");
        }
        // Also consider if the contest has already ended
        if (LocalDateTime.now().isAfter(contest.getEndTime())) {
            throw new RuntimeException("Contest with id-" + contestId + " has already ended!!!");
        }

        boolean isRegistered = contestUserRepo.findByUserAndContest(userId, contestId).isPresent();
        if (!isRegistered) {
            throw new RuntimeException("User is not registered for this contest.");
        }

        if (LocalDateTime.now().isBefore(contest.getStartTime())) {
            throw new RuntimeException("Contest with id-" + contestId + " has not started yet!!!");
        }
        // Also consider if the contest has already ended
        if (LocalDateTime.now().isAfter(contest.getEndTime())) {
            throw new RuntimeException("Contest with id-" + contestId + " has already ended!!!");
        }

        Optional<FullContestSubmission> existingSubmissionOpt =
                submissionRepo.findByContestIdAndUserId(contestId, userId);

        LocalDateTime now = LocalDateTime.now();
        FullContestSubmission submission;

        if (existingSubmissionOpt.isPresent()) {
            submission = existingSubmissionOpt.get();
            if (submission.getUserSubmittedAt() != null) {
                throw new RuntimeException("User has already submitted the contest.");
            }
            // Allow rejoining
        } else {
            submission = new FullContestSubmission();
            submission.setUser(user);
            submission.setContest(contest);
            submission.setUserStartedAt(now);
            submission.setUserSubmittedAt(null);
            submission.setPercentage(0.0);
            submission.setTotalTimeTaken(0L);
            submissionRepo.save(submission);
        }

        long durationSeconds = contest.getDuration(); // 👈 Duration now stored in seconds

        LocalDateTime userDeadline = submission.getUserStartedAt().plusSeconds(durationSeconds);
        LocalDateTime effectiveDeadline = userDeadline.isBefore(contest.getEndTime()) ? userDeadline : contest.getEndTime();

        long remainingTimeInSeconds = Duration.between(now, effectiveDeadline).getSeconds();
        if (remainingTimeInSeconds < 0) {
            remainingTimeInSeconds = 0; // Just in case user is too late
        }

        List<ContestProblem> contestProblemList = contestProblemRepo.findByContestIdNative(contestId);


        ContestStartResponseDTO responseDTO = new ContestStartResponseDTO();
        responseDTO.setContest(contest);
        responseDTO.setProblemList(contestProblemList);
        responseDTO.setRemainingTimeInSeconds(remainingTimeInSeconds); // 👈 Set remaining time

        return responseDTO;
    }

    public Problem getContestProblem(UUID problemId) {
        return problemService.getProblem(problemId);
    }

    public PartialContestSubmission addPartialContestSubmission(PartialContestSubmissionRequestDTO requestDTO, Problem problem, User user, Contest contest) {
        PartialContestSubmission partialContestSubmission = PartialContestSubmissionConverter.contestPartialSubmissionConverter(requestDTO, problem, user, contest);
        return partialContestSubmissionRepo.save(partialContestSubmission);
    }

    public PartialContestSubmission getPartialContestSubmission(UUID submissionId) {
        return partialContestSubmissionRepo.findById(submissionId).orElse(null);
    }

    public PartialContestSubmission updatePartialContestSubmission(UpdatePartialContestSubmissionRequestDTO requestDTO) {
        PartialContestSubmission submission = this.getPartialContestSubmission(requestDTO.getSubmissionId());

        if (submission == null) {
            throw new NotFoundException("Partial Submission with id-" + requestDTO.getSubmissionId() + " not Found!!!");
        }

        submission.setSubmissionStatus(requestDTO.getSubmissionStatus());
        submission.setCode(requestDTO.getCode());
        submission.setError(requestDTO.getError());
        submission.setPercentage(requestDTO.getPercentage());
        submission.setTotalTestCases(requestDTO.getTotalTestCases());
        submission.setTotalPassedTestCases(requestDTO.getTotalPassedTestCases());

        return partialContestSubmissionRepo.save(submission);
    }


    public FullContestSubmission submitContest(UUID userId, UUID contestId) {
        System.out.println("Contest Submission");
        Optional<FullContestSubmission> optionalSubmission =
                submissionRepo.findByContestIdAndUserId(contestId, userId);

        if (optionalSubmission.isEmpty()) {
            throw new IllegalArgumentException("User has not started the contest");
        }

        FullContestSubmission submission = optionalSubmission.get();

        if (submission.getUserSubmittedAt() != null) {
            throw new IllegalStateException("User has already submitted the contest");
        }

        List<ContestProblem> contestProblemList = contestProblemRepo.findByContestIdNative(contestId);
        List<Problem> allProblems = contestProblemList.stream()
                .map(ContestProblem::getProblem)
                .toList();

//        System.out.println("allProblems: "+allProblems);

        double totalPercentage = 0.0;
        int problemCount = allProblems.size();
        System.out.println("problemCount: " + problemCount);
//        System.out.println(allProblems.get(0));

        for (Problem problem : allProblems) {
            List<PartialContestSubmission> submissions = partialContestSubmissionRepo
                    .findByContestIdAndUserIdAndProblemId(contestId, userId, problem.getProblemId());

//            System.out.println("Submissions: "+submissions);
            if (!submissions.isEmpty()) {
                // Take the latest one or average one – here we use the latest submission
                PartialContestSubmission latest = submissions.get(submissions.size() - 1);
//                System.out.println(latest);
                totalPercentage += latest.getPercentage();
            } else {
                System.out.println("Empty Partial Submission List");
            }

        }
        System.out.println("totalPercentage: " + totalPercentage);

        double averagePercentage = problemCount > 0 ? (totalPercentage / problemCount) : 0.0;

        LocalDateTime submittedAt = LocalDateTime.now();
        submission.setUserSubmittedAt(submittedAt);
        submission.setTotalTimeTaken(Duration.between(submission.getUserStartedAt(), submittedAt).getSeconds()); //in seconds
        submission.setPercentage(averagePercentage);

        return submissionRepo.save(submission);
    }

    public void generateLeaderboard(UUID contestId) {
        // 1. Fetch contest
        Contest contest = contestRepo.findById(contestId)
                .orElseThrow(() -> new NotFoundException("Contest not found"));

        // 2. Check if leaderboard already exists
        if (!leaderboardEntryRepo.findByContestIdNative(contestId).isEmpty()) {
            throw new RuntimeException("Leaderboard already generated for this contest.");
        }

        // 3. Get all registered users
        List<ContestUser> registeredUsers = contestUserRepo.findAllByContestId(contestId);

        // 4. Get all submissions
        List<FullContestSubmission> allSubmissions = submissionRepo.findAllByContestId(contestId);

        // 5. Map userId -> submission
        Map<UUID, FullContestSubmission> submissionMap = allSubmissions.stream()
                .collect(Collectors.toMap(
                        s -> s.getUser().getUserId(),
                        s -> s
                ));

        // 6. Prepare leaderboard entries
        List<LeaderboardEntry> entries = new ArrayList<>();

        for (ContestUser cu : registeredUsers) {
            User user = cu.getUser();
            FullContestSubmission submission = submissionMap.get(user.getUserId());

            LeaderboardEntry entry = new LeaderboardEntry();
            entry.setContest(contest);
            entry.setUser(user);

            if (submission != null && submission.getUserSubmittedAt() != null) {
                entry.setPercentage(submission.getPercentage());
                entry.setTotalTimeTaken(submission.getTotalTimeTaken());
            } else {
                entry.setPercentage(0.0);
                entry.setTotalTimeTaken(contest.getDuration()); // Max duration if not submitted
            }

            entries.add(entry);
        }

        // 7. Sort with 3-level comparator: percentage ↓, time ↑, startedAt ↑
        entries.sort(Comparator
                .comparingDouble(LeaderboardEntry::getPercentage).reversed()
                .thenComparingLong(LeaderboardEntry::getTotalTimeTaken)
                .thenComparing(entry -> {
                    UUID userId = entry.getUser().getUserId();
                    FullContestSubmission submission = submissionMap.get(userId);
                    return (submission != null && submission.getUserStartedAt() != null)
                            ? submission.getUserStartedAt()
                            : LocalDateTime.MAX; // treat non-starters as very late
                }));

        // 8. Assign T- style ranks
        int currentRank = 1;
        int visibleRank = 1;
        LeaderboardEntry prev = null;

        for (LeaderboardEntry entry : entries) {
            if (prev != null &&
                    Double.compare(entry.getPercentage(), prev.getPercentage()) == 0 &&
                    entry.getTotalTimeTaken() == prev.getTotalTimeTaken()) {

                UUID userId = entry.getUser().getUserId();
                UUID prevUserId = prev.getUser().getUserId();

                LocalDateTime startA = submissionMap.containsKey(userId) && submissionMap.get(userId).getUserStartedAt() != null
                        ? submissionMap.get(userId).getUserStartedAt() : LocalDateTime.MAX;

                LocalDateTime startB = submissionMap.containsKey(prevUserId) && submissionMap.get(prevUserId).getUserStartedAt() != null
                        ? submissionMap.get(prevUserId).getUserStartedAt() : LocalDateTime.MAX;

                if (startA.equals(startB)) {
                    entry.setRank(prev.getRank()); // tie
                } else {
                    entry.setRank(visibleRank);
                }
            } else {
                entry.setRank(visibleRank);
            }

            prev = entry;
            currentRank++;
            visibleRank = currentRank;
        }

        // 9. Save to DB
        leaderboardEntryRepo.saveAll(entries);
    }

    public List<PastContestResponseListDTO> getPastContests(UUID userId) {
        List<Contest> pastContests = contestRepo.findAllPastContests(LocalDateTime.now());
        return buildPastContestDTOs(pastContests, userId);
    }

    public List<PastContestResponseListDTO> getPastContestsByPage(UUID userId, int pageNo) {
        Pageable pageable = PageRequest.of(pageNo - 1, 10); // page size 10
        Page<Contest> page = contestRepo.findAllPastContests(LocalDateTime.now(), pageable);
        return buildPastContestDTOs(page.getContent(), userId);
    }

    private List<PastContestResponseListDTO> buildPastContestDTOs(List<Contest> contests, UUID userId) {
        List<PastContestResponseListDTO> responseList = new ArrayList<>();

        for (Contest contest : contests) {
            boolean participated = contestUserRepo.findByUserAndContest(userId, contest.getContestId()).isPresent();


            PastContestResponseListDTO dto = new PastContestResponseListDTO();
            dto.setContest(contest);
            dto.setUserParticipated(participated);

            responseList.add(dto);
        }

        return responseList;
    }

    public PastContestResponseDTO getPastContestDetails(UUID userId, UUID contestId) {
        User user = userService.getUserById(userId);

        Contest contest = contestRepo.findById(contestId)
                .orElseThrow(() -> new NotFoundException("Contest with id-" + contestId + " not Found!!!"));

        if (LocalDateTime.now().isBefore(contest.getEndTime())) {
            throw new IllegalStateException("Contest has not ended yet.");
        }

        List<LeaderboardEntry> leaderboardEntries = leaderboardEntryRepo.findByContestIdNative(contestId);

        List<LeaderboardEntryResponseDTO> leaderboard = new ArrayList<>();
        boolean isUserParticipated = false;
        Integer userRank = null;

        for (LeaderboardEntry entry : leaderboardEntries) {
            if (entry.getUser().getUserId().equals(userId)) {
                isUserParticipated = true;
                userRank = entry.getRank();
            }

            leaderboard.add(new LeaderboardEntryResponseDTO(
                    entry.getRank(),
                    entry.getUser().getName(),
                    entry.getPercentage(),
                    entry.getTotalTimeTaken()
            ));
        }

        List<ContestProblem> contestProblemList = contestProblemRepo.findByContestIdNative(contestId);


        return new PastContestResponseDTO(
                contest,
                isUserParticipated,
                userRank,
                leaderboard,
                contestProblemList,
                leaderboardEntries.size()
        );
    }


}
