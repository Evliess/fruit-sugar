package evliess.io.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Request User Object")
public class UserDto {
  @Schema(description = "user identifier", example = "10086", requiredMode = Schema.RequiredMode.AUTO)
  private String name;
  @Schema(description = "user token", example = "token", requiredMode = Schema.RequiredMode.AUTO)
  private String code;
  @Schema(description = "token expire days", example = "7", requiredMode = Schema.RequiredMode.AUTO)
  private String days;
}
