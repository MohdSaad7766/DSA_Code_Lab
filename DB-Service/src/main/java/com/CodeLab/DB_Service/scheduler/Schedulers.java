package com.CodeLab.DB_Service.scheduler;

import com.CodeLab.DB_Service.integration.RabbitMQIntegration;
import com.CodeLab.DB_Service.model.Contest;
import com.CodeLab.DB_Service.model.User;
import com.CodeLab.DB_Service.repository.ContestRepo;
import com.CodeLab.DB_Service.repository.ContestUserRepo;
import com.CodeLab.DB_Service.requestDTO.ContestStartRequestDTO;
import com.CodeLab.DB_Service.service.ContestService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class Schedulers {

    @Autowired
    private ContestRepo contestRepo;

    @Autowired
    private ContestUserRepo contestUserRepo;

    @Autowired
    private ContestService contestService;

    @Autowired
    private RabbitMQIntegration rabbitMQIntegration;

    /**
     * This method runs every 10 seconds (after the previous execution completes).
     * It generates leaderboards for contests that have ended but don't yet have one.
     */
    @Scheduled(fixedDelay = 10000)
    @Transactional
    public void generateLeaderboardsForEndedContests() {
        System.out.println("Check for Leaderboard Generation");

        List<Contest> endedContests = contestRepo.findAllEndedContestsWithoutLeaderboard(LocalDateTime.now());

        if (endedContests.isEmpty()) {
            return;
        }

        for (Contest contest : endedContests) {
            try {
                contestService.generateLeaderboard(contest.getContestId());
                System.out.println("✅ Generated leaderboard for contest: " + contest.getContestName());
            } catch (Exception e) {
                System.err.println("❌ Failed to generate leaderboard for contest: " + contest.getContestName());
                e.printStackTrace();
            }
        }
    }

    /**
     * Sends start reminders to registered users for contests starting now.
     */
    @Scheduled(fixedDelay = 10000)
    @Transactional
    public void sendStartRemindersForContestsStartingNow() {
        System.out.println("Check for Contest Start Reminder Sent");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startWindow = now.minusSeconds(5);
        LocalDateTime endWindow = now.plusSeconds(10); // buffer to catch near-future contests

        List<Contest> startingContests = contestRepo.findContestsStartingBetween(startWindow, endWindow);

        for (Contest contest : startingContests) {
            if (contest.isStartReminderSent()) {
                continue; // skip if already sent
            }

            List<User> registeredUsers = contestUserRepo.findUsersByContestId(contest.getContestId());

            for (User user : registeredUsers) {
                ContestStartRequestDTO dto = processContest(contest, user);
                rabbitMQIntegration.sendContestStartMail(dto);
            }

            // Mark contest as notified
            contest.setStartReminderSent(true);
            contestRepo.save(contest);
        }
    }

    public ContestStartRequestDTO processContest(Contest contest, User user) {
        String userName = user.getName();
        String userEmail = user.getEmail();

        String contestName = contest.getContestName();
        String contestStartTime = processTime(contest.getStartTime());
        String contestEndTime = processTime(contest.getEndTime());
        String contestDuration = processDuration(contest.getDuration());

        return new ContestStartRequestDTO(userEmail, userName, contestName, contestStartTime, contestEndTime, contestDuration);
    }

    public String processTime(LocalDateTime time) {
        if (time == null) return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM yyyy, h:mm a");
        return time.format(formatter);
    }

    public String processDuration(Long durationInSeconds) {
        long minutes = durationInSeconds / 60;
        return minutes + " minutes";
    }
}
