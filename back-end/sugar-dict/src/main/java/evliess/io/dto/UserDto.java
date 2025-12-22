package evliess.io.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户创建请求对象")
public class UserDto {
  @Schema(description = "用户ID", example = "10086", requiredMode = Schema.RequiredMode.REQUIRED)
  private String name;
  @Schema(description = "用户ID", example = "10086", requiredMode = Schema.RequiredMode.REQUIRED)
  private String age;
}
