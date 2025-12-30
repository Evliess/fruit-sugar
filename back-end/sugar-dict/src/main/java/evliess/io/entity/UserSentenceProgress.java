package evliess.io.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_sentences_progress")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSentenceProgress {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "user_id")
  private Long userId;
  @Column(name = "module_id")
  private Long moduleId;
  @Column(name = "total_sentences_count")
  private Integer totalSentencesCount;
  @Column(name = "learned_sentences_count")
  private Integer learnedSentencesCount;
  @Column(name = "last_studied_at")
  private LocalDateTime lastStudiedAt;
}
