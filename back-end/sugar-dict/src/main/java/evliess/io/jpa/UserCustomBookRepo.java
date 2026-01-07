package evliess.io.jpa;

import evliess.io.entity.UserCustomBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserCustomBookRepo extends JpaRepository<UserCustomBook, Long> {

  @Query("SELECT a from UserCustomBook a WHERE a.userId= :userId AND a.wordId IS NOT NULL")
  List<UserCustomBook> getUserCustomWordsByUserId(Long userId);

  @Query("SELECT a from UserCustomBook a WHERE a.userId= :userId AND a.sentenceId IS NOT NULL")
  List<UserCustomBook> getUserCustomSentencesByUserId(Long userId);

  @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM UserCustomBook u WHERE u.userId = :userId AND u.wordId = :wordId")
  boolean existsByUserIdAndWordId(Long userId, Long wordId);

  @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM UserCustomBook u WHERE u.userId = :userId AND u.sentenceId = :sentenceId")
  boolean existsByUserIdAndSentenceId(Long userId, Long sentenceId);

  @Query("DELETE from UserCustomBook a WHERE a.userId= :userId AND a.wordId = :wordId")
  @Modifying
  int deleteUserCustomWordByUserIdAndWordId(Long userId, Long wordId);

  @Query("DELETE from UserCustomBook a WHERE a.userId= :userId AND a.sentenceId = :sentenceId")
  @Modifying
  int deleteUserCustomSentenceByUserIdAndSentenceId(Long userId, Long sentenceId);
}
