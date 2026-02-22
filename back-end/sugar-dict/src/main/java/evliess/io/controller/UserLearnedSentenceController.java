package evliess.io.controller;

import evliess.io.dto.UserLearnedDTO;
import evliess.io.service.UserLearnedSentenceSvc;
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
@Tag(name = "Users Learned Sentence Operations", description = "Users Learned Sentence Operations")
public class UserLearnedSentenceController {
  private final UserLearnedSentenceSvc userLearnedSentenceSvc;

  @Autowired
  public UserLearnedSentenceController(UserLearnedSentenceSvc userLearnedSentenceSvc) {
    this.userLearnedSentenceSvc = userLearnedSentenceSvc;
  }

  @Operation(summary = "Mark a unknown sentence", description = "Mark a unknown sentence")
  @PostMapping(value = "/sentence/mark-as-unknown", produces = "application/json")
  public ResponseEntity<String> markAsUnKnown(@RequestBody UserLearnedDTO userLearnedDTO) {
    return this.userLearnedSentenceSvc.markAsUnKnown(userLearnedDTO.getUserId(),
      userLearnedDTO.getSentenceId(), userLearnedDTO.getModuleId());
  }

  @Operation(summary = "Mark a known sentence", description = "Mark a known sentence")
  @PostMapping(value = "/sentence/mark-as-known", produces = "application/json")
  public ResponseEntity<String> markAsKnown(@RequestBody UserLearnedDTO userLearnedDTO) {
    return this.userLearnedSentenceSvc.markAsKnown(userLearnedDTO.getUserId(), userLearnedDTO.getSentenceId()
      , userLearnedDTO.getModuleId());
  }
}
