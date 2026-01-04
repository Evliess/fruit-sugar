package evliess.io.jpa;

import evliess.io.entity.UserSentenceProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserSentenceProgressRepo extends JpaRepository<UserSentenceProgress, Long> {
  UserSentenceProgress findByUserIdAndModuleId(Long userId, Long moduleId);

  @Modifying
  @Query("UPDATE UserSentenceProgress p SET p.learnedSentencesCount = p.learnedSentencesCount + 1, p.lastStudiedAt = CURRENT_TIMESTAMP WHERE p.userId = :userId AND p.moduleId = :moduleId")
  void incrementLearnedCount(@Param("userId") Long userId, @Param("moduleId") Long moduleId);

  @Modifying
  @Query("UPDATE UserSentenceProgress p SET p.learnedSentencesCount = p.learnedSentencesCount - 1, p.lastStudiedAt = CURRENT_TIMESTAMP WHERE p.userId = :userId AND p.moduleId = :moduleId")
  void decrementLearnedCount(@Param("userId") Long userId, @Param("moduleId") Long moduleId);

  @Modifying
  @Query("UPDATE UserSentenceProgress p SET p.learnedSentencesCount = 0, p.lastStudiedAt = CURRENT_TIMESTAMP WHERE p.userId = :userId AND p.moduleId = :moduleId")
  void resetLearnedCount(@Param("userId") Long userId, @Param("moduleId") Long moduleId);
}
