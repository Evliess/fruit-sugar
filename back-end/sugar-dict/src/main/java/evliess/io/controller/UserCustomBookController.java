package evliess.io.controller;


import evliess.io.service.UserCustomBookSvc;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/private/v1")
@Tag(name = "User Custom Book Operations", description = "User Custom Book Operations")
public class UserCustomBookController {

  private final UserCustomBookSvc userCustomBookSvc;

  public UserCustomBookController(UserCustomBookSvc userCustomBookSvc) {
    this.userCustomBookSvc = userCustomBookSvc;
  }

  @Operation(
    summary = "Get User Custom Words by UserId",
    description = "Get User Custom Words by UserId"
  )
  @GetMapping(value = "/user-custom-book/words/{userId}", produces = "application/json")
  public ResponseEntity<String> getCustomWords(@Parameter(description = "userId", required = true, example = "1")
                                               @PathVariable Long userId) {
    return this.userCustomBookSvc.buildWordResponseEntity(userId);
  }

  @Operation(
    summary = "Get User Custom Sentences by UserId",
    description = "Get User Custom Sentences by UserId"
  )
  @GetMapping(value = "/user-custom-book/sentences/{userId}", produces = "application/json")
  public ResponseEntity<String> getCustomSentences(@Parameter(description = "userId", required = true, example = "1")
                                                   @PathVariable Long userId) {
    return this.userCustomBookSvc.buildSentenceResponseEntity(userId);
  }

  @Operation(
    summary = "Delete Custom Word/Sentence by UserId and ObjId and Type",
    description = "Delete Custom Word/Sentence by UserId and ObjId and Type"
  )
  @DeleteMapping(value = "/user-custom-book/{userId}/{id}/{type}", produces = "application/json")
  public ResponseEntity<String> deleteWord(
    @Parameter(description = "userId", required = true, example = "1")
    @PathVariable Long userId,
    @Parameter(description = "id", required = true, example = "1")
    @PathVariable Long id,
    @Parameter(description = "type", required = true, example = "word")
    @PathVariable String type
  ) {
    return this.userCustomBookSvc.deleteUserCustomWordByUserIdAndWordId(userId, id, type);
  }
}
