package evliess.io.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "words")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Word {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "text", columnDefinition = "TEXT")
  private String text;

  @Column(name = "phonetic_us", columnDefinition = "TEXT")
  private String phoneticUS;

  @Column(name = "phonetic_uk", columnDefinition = "TEXT")
  private String phoneticUK;

  @Column(name = "definition", columnDefinition = "TEXT")
  private String definition;

  @Column(name = "audio_us_url")
  private String audioUSUrl;

  @Column(name = "audio_uk_url")
  private String audioUKUrl;

  @Column(name = "phrases", columnDefinition = "JSON")
  private String phrases;

  @Column(name = "example_sentences", columnDefinition = "JSON")
  private String sentences;

  @Column(name = "module_id")
  private Long moduleId;

}
