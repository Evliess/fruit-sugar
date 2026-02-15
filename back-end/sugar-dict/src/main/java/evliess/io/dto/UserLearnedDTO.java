package evliess.io.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "User Learned/Unknown/Mistake Object")
public class UserLearnedDTO {
  @Schema(description = "user id", example = "1", requiredMode = Schema.RequiredMode.AUTO)
  private Long userId;
  @Schema(description = "module id", example = "1", requiredMode = Schema.RequiredMode.AUTO)
  private Long moduleId;
  @Schema(description = "word id", example = "1", requiredMode = Schema.RequiredMode.AUTO)
  private Long wordId;
  @Schema(description = "sentence id", example = "1", requiredMode = Schema.RequiredMode.AUTO)
  private Long sentenceId;
  @Schema(description = "the type of listen progress", example = "word/sentence/custom_word/custom_sentence", requiredMode = Schema.RequiredMode.AUTO)
  private String type;
  @Schema(description = "the index of listen progress", example = "0", requiredMode = Schema.RequiredMode.AUTO)
  private Integer currIndex;

}
