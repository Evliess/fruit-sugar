package evliess.io.jpa;

import evliess.io.entity.UserUnknownWord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserUnknownWordRepo extends JpaRepository<UserUnknownWord, Long> {
  @Query("SELECT a from UserUnknownWord a WHERE a.userId= :userId")
  List<UserUnknownWord> getUserUnknownWordsByUserId(Long userId);

  @Query("DELETE from UserUnknownWord a WHERE a.userId= :userId AND a.wordId = :wordId")
  @Modifying
  int deleteUserUnknownWordsByUserIdAndWordId(Long userId, Long wordId);

  @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM UserUnknownWord u WHERE u.userId = :userId AND u.wordId = :wordId")
  boolean existsByUserIdAndWordId(Long userId, Long wordId);
}
