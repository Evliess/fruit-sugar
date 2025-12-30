package evliess.io.jpa;

import evliess.io.entity.UserLearnedWord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserLearnedWordRepo extends JpaRepository<UserLearnedWord, Long> {
  @Query("SELECT u.wordId FROM UserLearnedWord u WHERE u.userId = :userId AND u.moduleId = :moduleId")
  List<Long> findLearnedWordIds(@Param("userId") Long userId, @Param("moduleId") Long moduleId);

  @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM UserLearnedWord u WHERE u.userId = :userId AND u.wordId = :wordId")
  boolean existsByUserIdAndWordId(Long userId, Long wordId);
}
