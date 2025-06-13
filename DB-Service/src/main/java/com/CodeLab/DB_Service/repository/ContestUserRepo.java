package com.CodeLab.DB_Service.repository;

import com.CodeLab.DB_Service.model.ContestUser;
import com.CodeLab.DB_Service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ContestUserRepo extends JpaRepository<ContestUser, UUID> {
//    Optional<ContestUser> findByUserIdAndContestId(UUID userId, UUID contestId);

    @Query(value = "SELECT * FROM contest_user cu WHERE cu.user_id = :userId AND cu.contest_id = :contestId", nativeQuery = true)
    Optional<ContestUser> findByUserAndContest(@Param("userId") UUID userId, @Param("contestId") UUID contestId);

    @Query(value = "SELECT * FROM contest_user cu WHERE cu.contest_id = :contestId", nativeQuery = true)
    List<ContestUser> findAllByContestId(@Param("contestId") UUID contestId);

    @Query(value = """
    SELECT u.* FROM users u
    INNER JOIN contest_user cu ON cu.user_id = u.user_id
    WHERE cu.contest_id = :contestId
""", nativeQuery = true)
    List<User> findUsersByContestId(@Param("contestId") UUID contestId);
}
