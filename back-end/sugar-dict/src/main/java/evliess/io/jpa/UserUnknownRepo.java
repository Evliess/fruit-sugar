package evliess.io.jpa;

import evliess.io.entity.UserUnknown;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserUnknownRepo extends JpaRepository<UserUnknown, Long> {
  @Query("SELECT a from UserUnknown a WHERE a.userId= :userId AND a.wordId IS NOT NULL")
  List<UserUnknown> getUserUnknownWordsByUserId(Long userId);

  @Query("SELECT a from UserUnknown a WHERE a.userId= :userId AND a.sentenceId IS NOT NULL")
  List<UserUnknown> getUserUnknownSentencesByUserId(Long userId);

  @Query("SELECT count(a.wordId) from UserUnknown a WHERE a.userId= :userId AND a.wordId IS NOT NULL")
  Integer countUserUnknownWordsByUserId(Long userId);

  @Query("SELECT count(a.sentenceId) from UserUnknown a WHERE a.userId= :userId AND a.sentenceId IS NOT NULL")
  Integer countUserUnknownSentencesByUserId(Long userId);

  @Query("DELETE from UserUnknown a WHERE a.userId= :userId AND a.wordId = :wordId")
  @Modifying
  int deleteUserUnknownWordByUserIdAndWordId(Long userId, Long wordId);

  @Query("DELETE from UserUnknown a WHERE a.userId= :userId AND a.sentenceId = :sentenceId")
  @Modifying
  int deleteUserUnknownSentenceByUserIdAndSentenceId(Long userId, Long sentenceId);

  @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM UserUnknown u WHERE u.userId = :userId AND u.wordId = :wordId")
  boolean existsByUserIdAndWordId(Long userId, Long wordId);

  @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM UserUnknown u WHERE u.userId = :userId AND u.sentenceId = :sentenceId")
  boolean existsByUserIdAndSentenceId(Long userId, Long sentenceId);
}
