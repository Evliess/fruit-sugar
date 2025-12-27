package evliess.io.jpa;

import evliess.io.entity.Sentence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface SentenceRepo extends JpaRepository<Sentence, Long> {

  @Query("SELECT a from Sentence a WHERE a.moduleId= :moduleId")
  List<Sentence> getSentencesByModuleId(Long moduleId);


}
