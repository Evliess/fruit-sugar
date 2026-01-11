package evliess.io.jpa;

import evliess.io.entity.UserLearnedWord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserLearnedWordRepo extends JpaRepository<UserLearnedWord, Long> {
  @Query("SELECT DISTINCT u.wordId FROM UserLearnedWord u WHERE u.userId = :userId AND u.moduleId = :moduleId")
  List<Long> findLearnedWordIds(@Param("userId") Long userId, @Param("moduleId") Long moduleId);

  @Query("SELECT count(u.wordId) FROM UserLearnedWord u WHERE u.userId = :userId")
  Integer countLearnedWordsByUserId(@Param("userId") Long userId);

  @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM UserLearnedWord u WHERE u.userId = :userId AND u.wordId = :wordId")
  boolean existsByUserIdAndWordId(Long userId, Long wordId);

  @Modifying
  @Transactional
  @Query("DELETE FROM UserLearnedWord u WHERE u.userId = :userId AND u.wordId = :wordId")
  int deleteByUserIdAndWordId(Long userId, Long wordId);

  @Modifying
  @Transactional
  @Query("DELETE FROM UserLearnedWord u WHERE u.userId = :userId AND u.moduleId = :moduleId")
  int deleteByUserIdAndModuleId(Long userId, Long moduleId);


}
