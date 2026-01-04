package evliess.io.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_learned_sentences")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLearnedSentence {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "user_id")
  private Long userId;
  @Column(name = "module_id")
  private Long moduleId;
  @Column(name = "sentence_id")
  private Long sentenceId;
  @Column(name = "learned_at")
  private LocalDateTime learnedAt;
}
