package com.CodeLab.DB_Service.repository;

import com.CodeLab.DB_Service.model.Problem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProblemRepo extends JpaRepository<Problem, UUID> {

    @Query(value = "SELECT * FROM Problems WHERE topic_list LIKE %:topicName% AND is_visible = true", nativeQuery = true)
    List<Problem> findByTopicName(@Param("topicName") String topicName);

    @Query(value = "SELECT COUNT(*) FROM Problems WHERE topic_list LIKE %:topicName% AND is_visible = true", nativeQuery = true)
    long countByTopicName(@Param("topicName") String topicName);

    @Query(value = "SELECT * FROM Problems WHERE company_list LIKE %:companyName% AND is_visible = true", nativeQuery = true)
    List<Problem> findByCompanyName(@Param("companyName") String companyName);

    @Query(value = "SELECT COUNT(*) FROM Problems WHERE company_list LIKE %:companyName% AND is_visible = true", nativeQuery = true)
    long countByCompanyName(@Param("companyName") String companyName);

    @Query(value = "SELECT DISTINCT * FROM problems " +
            "WHERE is_visible = true AND " +
            "(" +
            "LOWER(problem_title) LIKE %:keyword% OR " +
            "LOWER(topic_list) LIKE %:keyword% OR " +
            "LOWER(company_list) LIKE %:keyword% OR " +
            "CAST(problem_no AS TEXT) LIKE %:keyword%" +
            ")",
            nativeQuery = true)
    List<Problem> searchProblemsNative(@Param("keyword") String keyword);

    @Query(value = "SELECT DISTINCT * FROM problems " +
            "WHERE is_visible = true AND " +
            "(" +
            "LOWER(problem_title) LIKE %:keyword% OR " +
            "LOWER(topic_list) LIKE %:keyword% OR " +
            "LOWER(company_list) LIKE %:keyword% OR " +
            "CAST(problem_no AS TEXT) LIKE %:keyword%" +
            ") " +
            "ORDER BY problem_no",
            countQuery = "SELECT COUNT(*) FROM problems " +
                    "WHERE is_visible = true AND " +
                    "(" +
                    "LOWER(problem_title) LIKE %:keyword% OR " +
                    "LOWER(topic_list) LIKE %:keyword% OR " +
                    "LOWER(company_list) LIKE %:keyword% OR " +
                    "CAST(problem_no AS TEXT) LIKE %:keyword%" +
                    ")",
            nativeQuery = true)
    Page<Problem> searchProblemsWithPagination(@Param("keyword") String keyword, Pageable pageable);

    @Query(value = "SELECT DISTINCT * FROM problems " +
            "WHERE is_visible = true AND " +
            "(" +
            "LOWER(problem_title) LIKE %:keyword% OR " +
            "LOWER(topic_list) LIKE %:keyword% OR " +
            "LOWER(company_list) LIKE %:keyword% OR " +
            "CAST(problem_no AS TEXT) LIKE %:keyword%" +
            ") " +
            "ORDER BY problem_no",
            nativeQuery = true)
    List<Problem> searchProblemsWithoutPagination(@Param("keyword") String keyword);



    @Query(value = "SELECT * FROM Problems WHERE problem_difficulty = :difficulty AND is_visible = true", nativeQuery = true)
    List<Problem> findProblemByDifficulty(@Param("difficulty") String difficulty);

    @Query(value = "SELECT * FROM Problems WHERE is_visible = true", nativeQuery = true)
    List<Problem> getAllVisibleProblems();

    Page<Problem> findAllByIsVisibleTrue(Pageable pageable);
}
