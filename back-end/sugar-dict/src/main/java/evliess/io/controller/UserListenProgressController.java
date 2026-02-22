package evliess.io.controller;

import evliess.io.dto.UserLearnedDTO;
import evliess.io.service.UserListenProgressSvc;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/private/v1")
@Tag(name = "Users Listen-Write Progress Operations", description = "Users Listen-Write Progress Operations")
public class UserListenProgressController {
  private final UserListenProgressSvc userListenProgressSvc;

  @Autowired
  public UserListenProgressController(UserListenProgressSvc userListenProgressSvc) {
    this.userListenProgressSvc = userListenProgressSvc;
  }

  @Operation(summary = "Get user listen-write progress", description = "Get user listen-write progress")
  @GetMapping(value = "/user/listen/progress/{userId}/{moduleId}/{type}", produces = "application/json")
  public ResponseEntity<String> getIndex(@Parameter(description = "userId", required = true, example = "1") @PathVariable Long userId,
                                         @Parameter(description = "moduleId", required = true, example = "19") @PathVariable Long moduleId,
                                         @Parameter(description = "type", required = true, example = "word/sentence/custom_word/custom_sentence") @PathVariable String type) {
    UserLearnedDTO userLearnedDTO = new UserLearnedDTO();
    userLearnedDTO.setUserId(userId);
    userLearnedDTO.setModuleId(moduleId);
    userLearnedDTO.setType(type);
    return this.userListenProgressSvc.getIndex(userLearnedDTO);
  }

  @Operation(summary = "Save or Update user listen-write progress", description = "Get user listen-write progress")
  @PostMapping(value = "/user/listen/progress", produces = "application/json")
  public ResponseEntity<String> saveOrUpdate(@RequestBody UserLearnedDTO userLearnedDTO) {
    return this.userListenProgressSvc.saveOrUpdate(userLearnedDTO);
  }


}
