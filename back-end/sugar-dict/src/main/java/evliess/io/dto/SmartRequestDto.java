package evliess.io.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "AI Smart Request Object")
public class SmartRequestDto {
  @Schema(description = "The content to be analyzed (word or sentence)", example = "Hello, how are you?", requiredMode = Schema.RequiredMode.REQUIRED)
  private String content;

  @Schema(description = "The user identifier", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
  private Integer userId;
}