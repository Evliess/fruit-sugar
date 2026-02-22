package evliess.io.controller;

import evliess.io.service.UserUnknownSvc;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/private/v1")
@Tag(name = "Users Unknown Operations", description = "Users Unknown Operations")
public class UserUnknownController {

  private final UserUnknownSvc userUnknownSvc;

  @Autowired
  public UserUnknownController(UserUnknownSvc userUnknownSvc) {
    this.userUnknownSvc = userUnknownSvc;
  }

  @Operation(summary = "Delete by UserID and WordId", description = "Delete by userID and wordId")
  @DeleteMapping("/user-unknown-word/{userId}/{wordId}")
  public ResponseEntity<String> remove(
    @Parameter(description = "userId", required = true, example = "1")
    @PathVariable Long userId,
    @Parameter(description = "wordId", required = true, example = "1")
    @PathVariable Long wordId
  ) {
    return this.userUnknownSvc.remove(userId, wordId);
  }

  @Operation(summary = "Delete by UserID and sentenceId", description = "Delete by userID and sentenceId")
  @DeleteMapping("/user-unknown-sentence/{userId}/{sentenceId}")
  public ResponseEntity<String> removeSentence(
    @Parameter(description = "userId", required = true, example = "1")
    @PathVariable Long userId,
    @Parameter(description = "sentenceId", required = true, example = "1")
    @PathVariable Long sentenceId
  ) {
    return this.userUnknownSvc.removeSentence(userId, sentenceId);
  }

  @Operation(
    summary = "Get All Unknown Words and Sentences by User Id",
    description = "Get All Unknown Words and Sentences by User Id"
  )
  @GetMapping(value = "/user-unknown/user-id/{userId}", produces = "application/json")
  public ResponseEntity<String> getUnknownByUserId(
    @Parameter(description = "userId", required = true, example = "1")
    @PathVariable Long userId
  ) {
    return this.userUnknownSvc.getUnknownByUserId(userId);
  }

}
