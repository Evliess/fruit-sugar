package evliess.io.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Text for Tts")
public class TextDto {
  @Schema(description = "text for tts", example = "hello", requiredMode = Schema.RequiredMode.REQUIRED)
  private String text;
}
