package evliess.io.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_learned_words")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLearnedWord {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "user_id")
  private Long userId;
  @Column(name = "module_id")
  private Long moduleId;
  @Column(name = "word_id")
  private Integer wordId;
  @Column(name = "learned_at")
  private LocalDateTime learnedAt;
}
