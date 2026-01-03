package evliess.io.controller;

import evliess.io.dto.TextDto;
import evliess.io.service.DaoYouSvc;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public/v1")
@Tag(name = "YouDao Cloud Operations", description = "YouDao Cloud Operations")
public class DaoYouController {

  private final DaoYouSvc daoYouSvc;

  @Autowired
  public DaoYouController(DaoYouSvc daoYouSvc) {
    this.daoYouSvc = daoYouSvc;
  }


  // PUT /api/users/{id}
  @Operation(summary = "更新用户信息", description = "根据ID全量更新用户数据")
  @PutMapping("/{id}")
  public ResponseEntity<String> updateUser(
    @Parameter(description = "要更新的用户ID", required = true, example = "123")
    @PathVariable Long id,
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
      description = "更新后的用户数据",
      required = true,
      content = @Content(schema = @Schema(implementation = String.class))
    )
    @RequestBody String user
  ) {
    // 实现略
    return null;
  }

  @Operation(
    summary = "Get Digest by Text",
    description = "Get Digest by Text"
  )

  @PostMapping(value = "/text/digest", produces = "application/json")
  public ResponseEntity<String> getTextDigest(
    @RequestBody TextDto textDto
  ) {
    return this.daoYouSvc.getDigest(textDto.getText());
  }

  @Operation(summary = "Get Voice by Text", description = "Get Voice by Text")
  @PostMapping("/text/tts")
  public ResponseEntity<String> getTextTts(@RequestBody TextDto textDto) {
    return this.daoYouSvc.getTextTts(textDto.getText());
  }

}
