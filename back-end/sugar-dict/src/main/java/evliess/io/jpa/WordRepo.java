package evliess.io.jpa;

import evliess.io.entity.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface WordRepo extends JpaRepository<Word, Long> {
  @Query("SELECT a from Word a WHERE a.moduleId= :moduleId")
  List<Word> getWordsByModuleId(Long moduleId);

  @Query("SELECT count(a.id) from Word a WHERE a.moduleId= :moduleId")
  int countWordsByModuleId(Long moduleId);

  @Modifying
  @Query("UPDATE Word a set a.audioUSUrl = :url, a.audioUSUrl = :url WHERE a.text = :text")
  @Transactional
  int updateAudioUrlByText(String url, String text);

  @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM Word u WHERE u.text = :text")
  boolean existsByText(String text);

  @Query("SELECT a from Word a WHERE a.text= :text")
  Word getByText(String text);

  @Modifying
  @Query("UPDATE Word a set a.audioUSUrl = :base64StrUS, a.audioUKUrl = :base64StrUk WHERE a.id = :id")
  @Transactional
  void updateAudioUrlBase64(String base64StrUS, String base64StrUk, Long id);

  @Query("SELECT a from Word a WHERE a.id >= :start and a.id <= :end")
  List<Word> getByIDRange(Long start, Long end);
}
