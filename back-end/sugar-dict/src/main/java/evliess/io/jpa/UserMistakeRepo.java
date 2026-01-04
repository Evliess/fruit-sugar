package evliess.io.jpa;

import evliess.io.entity.UserMistake;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserMistakeRepo extends JpaRepository<UserMistake, Long> {
  @Query("SELECT a from UserMistake a WHERE a.userId= :userId AND a.wordId IS NOT NULL")
  List<UserMistake> getUserMistakeWordsByUserId(Long userId);

  @Query("SELECT a from UserMistake a WHERE a.userId= :userId AND a.sentenceId IS NOT NULL")
  List<UserMistake> getUserMistakeSentencesByUserId(Long userId);

  @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM UserMistake u WHERE u.userId = :userId AND u.wordId = :wordId")
  boolean existsByUserIdAndWordId(Long userId, Long wordId);

  @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM UserMistake u WHERE u.userId = :userId AND u.sentenceId = :sentenceId")
  boolean existsByUserIdAndSentenceId(Long userId, Long sentenceId);


  @Query("DELETE from UserMistake a WHERE a.userId= :userId AND a.wordId = :wordId")
  @Modifying
  int deleteUserMistakeWordByUserIdAndWordId(Long userId, Long wordId);

  @Query("DELETE from UserMistake a WHERE a.userId= :userId AND a.sentenceId = :sentenceId")
  @Modifying
  int deleteUserMistakeSentenceByUserIdAndSentenceId(Long userId, Long sentenceId);
}
