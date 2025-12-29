package evliess.io.controller;

import evliess.io.dto.TextDto;
import evliess.io.dto.UserDto;
import evliess.io.service.DaoYouSvc;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/public/v1")
@Tag(name = "YouDao Cloud Operations", description = "YouDao Cloud Operations")
public class DaoYouController {

  private final DaoYouSvc daoYouSvc;

  @Autowired
  public DaoYouController(DaoYouSvc daoYouSvc) {
    this.daoYouSvc = daoYouSvc;
  }

  @Operation(summary = "根据ID获取用户", description = "返回指定ID的用户信息")
  @GetMapping("/stock/{id}")
  public ResponseEntity<String> getUserById(
    @Parameter(description = "用户ID", required = true, example = "123")
    @PathVariable Long id
  ) {
    // 实现略
    return null;
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
    summary = "Download single word voice by type",
    description = "Download single word voice by type"
  )
  @ApiResponse(
    responseCode = "200",
    description = "媒体文件下载成功",
    content = @Content(
      mediaType = "audio/mpeg",
      schema = @Schema(type = "string", format = "binary")
    )
  )
  @ApiResponse(responseCode = "400", description = "请求参数无效")
  @ApiResponse(responseCode = "404", description = "媒体文件不存在")
  @GetMapping(value = "/word-voice/download", produces = "audio/mpeg")
  public ResponseEntity<Resource> downloadSingleWordVoice(
    @Parameter(description = "The word", required = true, example = "hello")
    @RequestParam String audio,

    @Parameter(description = "The type of voice", required = true, example = "1/2")
    @RequestParam String type
  ) throws Exception {
    // 构造文件路径（示例）
    String filename = type + ".mp3"; // 或 .mpeg
    Path filePath = Paths.get("/media/", audio, filename);

    if (!Files.exists(filePath)) {
      throw new RuntimeException("媒体文件未找到");
    }

    byte[] fileBytes = Files.readAllBytes(filePath);
    ByteArrayResource resource = new ByteArrayResource(fileBytes) {
      @Override
      public String getFilename() {
        return filename;
      }
    };

    return ResponseEntity.ok()
      .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"") // 注意：这里用 inline 可在浏览器播放
      .contentType(MediaType.parseMediaType("audio/mpeg"))
      .contentLength(fileBytes.length)
      .body(resource);
  }

  // DELETE /api/users/{id}
  @Operation(summary = "删除用户", description = "根据ID删除指定用户")
  @ApiResponse(responseCode = "204", description = "删除成功（无内容返回）")
  @ApiResponse(responseCode = "404", description = "用户不存在")
  @DeleteMapping("/{id}")
  public ResponseEntity<String> deleteUser(
    @Parameter(description = "要删除的用户ID", required = true, example = "123")
    @PathVariable Long id
  ) {
    // 实现略
    return null;
  }

  @PostMapping("/create")
  @Operation(summary = "创建新用户", description = "接收JSON格式的name和id来创建用户")
  public String createUser(
    @RequestBody UserDto request
  ) {
    // 模拟业务逻辑
    System.out.println("接收到的Name: " + request.getName());

    return "用户 " + request.getName() + " 创建成功";
  }

  @Operation(summary = "Get Voice by Text", description = "Get Voice by Text")
  @PostMapping("/text/tts")
  public ResponseEntity<String> getTextTts(@RequestBody TextDto textDto) {
    return this.daoYouSvc.getTextTts(textDto.getText());
  }

}
