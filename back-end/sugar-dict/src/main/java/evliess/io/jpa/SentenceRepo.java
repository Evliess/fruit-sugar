package evliess.io.jpa;

import evliess.io.entity.Sentence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SentenceRepo extends JpaRepository<Sentence, Long> {

}
