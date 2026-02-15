package evliess.io.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_listen_progress")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserListenProgress {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "user_id")
  private Long userId;
  @Column(name = "module_id")
  private Long moduleId;
  @Column(name = "type")
  private String type;
  @Column(name = "curr_index")
  private Integer currIndex;

}
