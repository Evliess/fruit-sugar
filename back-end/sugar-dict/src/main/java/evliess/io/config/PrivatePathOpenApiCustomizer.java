package evliess.io.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.stereotype.Component;

@Component
public class PrivatePathOpenApiCustomizer implements OpenApiCustomizer {
  private static final String SECURITY_SCHEME_NAME = "bearerAuth";

  @Override
  public void customise(OpenAPI openApi) {
    // 1. 确保 Components 对象存在
    if (openApi.getComponents() == null) {
      openApi.setComponents(new io.swagger.v3.oas.models.Components());
    }

    // 2. 检查并添加安全组件
    // 使用 getSecuritySchemes() 来检查是否存在
    if (openApi.getComponents().getSecuritySchemes() == null
      || !openApi.getComponents().getSecuritySchemes().containsKey(SECURITY_SCHEME_NAME)) {

      openApi.getComponents().addSecuritySchemes(SECURITY_SCHEME_NAME,
        new SecurityScheme()
          .type(SecurityScheme.Type.HTTP)
          .scheme("bearer")
          .bearerFormat("JWT")
          .description("请输入 JWT Token (格式: Bearer your-token-here)")
      );
    }

    // 3. 遍历所有路径，只给以 /private 开头的路径添加安全要求
    if (openApi.getPaths() != null) {
      openApi.getPaths().forEach((path, pathItem) -> {
        if (path.startsWith("/private")) {
          addSecurityToOperations(pathItem, SECURITY_SCHEME_NAME);
        }
      });
    }
  }

  private void addSecurityToOperations(PathItem pathItem, String securitySchemeName) {
    SecurityRequirement securityRequirement = new SecurityRequirement().addList(securitySchemeName);

    // 对每种 HTTP 方法进行操作
    if (pathItem.getGet() != null) {
      pathItem.getGet().addSecurityItem(securityRequirement);
    }
    if (pathItem.getPost() != null) {
      pathItem.getPost().addSecurityItem(securityRequirement);
    }
    if (pathItem.getPut() != null) {
      pathItem.getPut().addSecurityItem(securityRequirement);
    }
    if (pathItem.getDelete() != null) {
      pathItem.getDelete().addSecurityItem(securityRequirement);
    }
    if (pathItem.getPatch() != null) {
      pathItem.getPatch().addSecurityItem(securityRequirement);
    }
    if (pathItem.getHead() != null) {
      pathItem.getHead().addSecurityItem(securityRequirement);
    }
    if (pathItem.getOptions() != null) {
      pathItem.getOptions().addSecurityItem(securityRequirement);
    }
    if (pathItem.getTrace() != null) {
      pathItem.getTrace().addSecurityItem(securityRequirement);
    }
  }
}
