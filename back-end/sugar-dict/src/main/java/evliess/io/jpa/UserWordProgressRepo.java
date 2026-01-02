package evliess.io.jpa;

import evliess.io.entity.UserWordProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserWordProgressRepo extends JpaRepository<UserWordProgress, Long> {
  UserWordProgress findByUserIdAndModuleId(Long userId, Long moduleId);

  @Modifying
  @Query("UPDATE UserWordProgress p SET p.learnedWordsCount = p.learnedWordsCount + 1, p.lastStudiedAt = CURRENT_TIMESTAMP WHERE p.userId = :userId AND p.moduleId = :moduleId")
  void incrementLearnedCount(@Param("userId") Long userId, @Param("moduleId") Long moduleId);

  @Modifying
  @Query("UPDATE UserWordProgress p SET p.learnedWordsCount = p.learnedWordsCount - 1, p.lastStudiedAt = CURRENT_TIMESTAMP WHERE p.userId = :userId AND p.moduleId = :moduleId")
  void decrementLearnedCount(@Param("userId") Long userId, @Param("moduleId") Long moduleId);

  @Modifying
  @Query("UPDATE UserWordProgress p SET p.learnedWordsCount = 0, p.lastStudiedAt = CURRENT_TIMESTAMP WHERE p.userId = :userId AND p.moduleId = :moduleId")
  void resetLearnedCount(@Param("userId") Long userId, @Param("moduleId") Long moduleId);
}
