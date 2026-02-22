package evliess.io.controller;

import evliess.io.dto.UserLearnedDTO;
import evliess.io.service.UserLearnedWordSvc;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/private/v1")
@Tag(name = "Users Learned Word Operations", description = "Users Learned Word Operations")
public class UserLearnedWordController {

  private UserLearnedWordSvc userLearnedWordSvc;

  @Autowired
  public UserLearnedWordController(UserLearnedWordSvc userLearnedWordSvc) {
    this.userLearnedWordSvc = userLearnedWordSvc;
  }

  @Operation(summary = "Mark a unknown word", description = "Mark a unknown word")
  @PostMapping(value = "/word/mark-as-unknown", produces = "application/json")
  public ResponseEntity<String> markAsUnKnown(@RequestBody UserLearnedDTO userLearnedDTO) {
    return this.userLearnedWordSvc.markAsUnKnown(userLearnedDTO.getUserId(),
      userLearnedDTO.getWordId(), userLearnedDTO.getModuleId());
  }

  @Operation(summary = "Mark a known word", description = "Mark a known word")
  @PostMapping(value = "/word/mark-as-known", produces = "application/json")
  public ResponseEntity<String> markAsKnown(@RequestBody UserLearnedDTO userLearnedDTO) {
    return this.userLearnedWordSvc.markAsKnown(userLearnedDTO.getUserId(), userLearnedDTO.getWordId()
      , userLearnedDTO.getModuleId());
  }
}
