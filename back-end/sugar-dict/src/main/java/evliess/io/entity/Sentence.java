package evliess.io.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sentences")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Sentence {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "text", columnDefinition = "TEXT")
  private String text;

  @Column(name = "text_translation", columnDefinition = "TEXT")
  private String textTranslation;

  @Column(name = "audio_us_url")
  private String audioUSUrl;

  @Column(name = "audio_uk_url")
  private String audioUKUrl;

  @Column(name = "module_id")
  private Long moduleId;
}
