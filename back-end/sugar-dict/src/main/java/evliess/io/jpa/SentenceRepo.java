package evliess.io.jpa;

import evliess.io.entity.Sentence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SentenceRepo extends JpaRepository<Sentence, Long> {

  @Query("SELECT a from Sentence a WHERE a.moduleId= :moduleId")
  List<Sentence> getSentencesByModuleId(Long moduleId);

  @Query("SELECT count(a.id) from Sentence a WHERE a.moduleId= :moduleId")
  int countSentencesByModuleId(Long moduleId);

  @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM Sentence u WHERE u.text = :text")
  boolean existsByText(String text);

  @Query("SELECT a from Sentence a WHERE a.text= :text")
  Sentence getByText(String text);


}
