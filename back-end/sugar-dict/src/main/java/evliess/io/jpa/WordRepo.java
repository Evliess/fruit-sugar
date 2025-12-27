package evliess.io.jpa;

import evliess.io.entity.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WordRepo extends JpaRepository<Word, Long> {
  @Query("SELECT a from Word a WHERE a.moduleId= :moduleId")
  List<Word> getWordsByModuleId(Long moduleId);
}
