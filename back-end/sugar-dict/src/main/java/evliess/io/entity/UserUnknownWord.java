package evliess.io.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_unknown_words")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUnknownWord {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "user_id")
  private Long userId;
  @Column(name = "module_id")
  private Long moduleId;
  @Column(name = "word_id")
  private Long wordId;
  @Column(name = "created_at")
  private LocalDateTime createdAt;
}
