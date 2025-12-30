package evliess.io.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_words_progress")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserWordProgress {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "user_id")
  private Long userId;
  @Column(name = "module_id")
  private Long moduleId;
  @Column(name = "total_words_count")
  private Integer totalWordsCount;
  @Column(name = "learned_words_count")
  private Integer learnedWordsCount;
  @Column(name = "last_studied_at")
  private LocalDateTime lastStudiedAt;
}
