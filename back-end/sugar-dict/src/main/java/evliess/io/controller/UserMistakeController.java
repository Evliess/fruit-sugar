package evliess.io.controller;

import evliess.io.dto.UserLearnedDTO;
import evliess.io.service.UserMistakeSvc;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/private/v1")
@Tag(name = "Users Mistake Operations", description = "Users Unknown Operations")
public class UserMistakeController {
  private final UserMistakeSvc userMistakeSvc;

  @Autowired
  public UserMistakeController(UserMistakeSvc userMistakeSvc) {
    this.userMistakeSvc = userMistakeSvc;
  }

  @Operation(summary = "Delete by UserID and WordId", description = "Delete by userID and wordId")
  @DeleteMapping("/user-mistake-word/{userId}/{wordId}")
  public ResponseEntity<String> remove(
    @Parameter(description = "userId", required = true, example = "1")
    @PathVariable Long userId,
    @Parameter(description = "wordId", required = true, example = "1")
    @PathVariable Long wordId
  ) {
    return this.userMistakeSvc.remove(userId, wordId);
  }

  @Operation(summary = "Delete by UserID and sentenceId", description = "Delete by userID and sentenceId")
  @DeleteMapping("/user-mistake-sentence/{userId}/{sentenceId}")
  public ResponseEntity<String> removeSentence(
    @Parameter(description = "userId", required = true, example = "1")
    @PathVariable Long userId,
    @Parameter(description = "sentenceId", required = true, example = "1")
    @PathVariable Long sentenceId
  ) {
    return this.userMistakeSvc.removeSentence(userId, sentenceId);
  }

  @Operation(
    summary = "Get All Mistake Words and Sentences by User Id",
    description = "Get All Mistake Words and Sentences by User Id"
  )
  @GetMapping(value = "/user-mistake/user-id/{userId}", produces = "application/json")
  public ResponseEntity<String> getUnknownByUserId(
    @Parameter(description = "userId", required = true, example = "1")
    @PathVariable Long userId
  ) {
    return this.userMistakeSvc.getUnknownByUserId(userId);
  }

  @Operation(summary = "Mark a Mistake word", description = "Mark a Mistake word")
  @PostMapping(value = "/word/mark-as-mistake", produces = "application/json")
  public ResponseEntity<String> markAsMistake(@RequestBody UserLearnedDTO userLearnedDTO) {
    return this.userMistakeSvc.markAsMistake(userLearnedDTO.getUserId(), userLearnedDTO.getWordId()
      , userLearnedDTO.getModuleId());
  }

  @Operation(summary = "Mark a Mistake Sentence", description = "Mark a Mistake Sentence")
  @PostMapping(value = "/sentence/mark-as-mistake", produces = "application/json")
  public ResponseEntity<String> markSentenceAsMistake(@RequestBody UserLearnedDTO userLearnedDTO) {
    return this.userMistakeSvc.markAsMistakeSentence(userLearnedDTO.getUserId(), userLearnedDTO.getSentenceId()
      , userLearnedDTO.getModuleId());
  }
}
