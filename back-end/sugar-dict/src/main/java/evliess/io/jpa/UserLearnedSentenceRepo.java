package evliess.io.jpa;

import evliess.io.entity.UserLearnedSentence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserLearnedSentenceRepo extends JpaRepository<UserLearnedSentence, Long> {
  @Query("SELECT DISTINCT u.sentenceId FROM UserLearnedSentence u WHERE u.userId = :userId AND u.moduleId = :moduleId")
  List<Long> findLearnedSentenceIds(@Param("userId") Long userId, @Param("moduleId") Long moduleId);

  @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM UserLearnedSentence u WHERE u.userId = :userId AND u.sentenceId = :sentenceId")
  boolean existsByUserIdAndSentenceId(Long userId, Long sentenceId);

  @Modifying
  @Transactional
  @Query("DELETE FROM UserLearnedSentence u WHERE u.userId = :userId AND u.sentenceId = :sentenceId")
  int deleteByUserIdAndSentenceId(Long userId, Long sentenceId);

  @Modifying
  @Transactional
  @Query("DELETE FROM UserLearnedSentence u WHERE u.userId = :userId AND u.moduleId = :moduleId")
  int deleteByUserIdAndModuleId(Long userId, Long moduleId);
}
