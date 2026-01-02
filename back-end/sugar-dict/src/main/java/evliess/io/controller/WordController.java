package evliess.io.controller;

import evliess.io.service.WordSvc;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public/v1")
@Tag(name = "Word Operations", description = "Word Operations")
public class WordController {

  private final WordSvc wordSvc;

  @Autowired
  public WordController(WordSvc wordSvc) {
    this.wordSvc = wordSvc;
  }

  @Operation(
    summary = "Get All Words Information by Module Id",
    description = "Get All Words Information by Module Id"
  )
  @GetMapping(value = "/words/by-child-content-module-id", produces = "application/json")
  public ResponseEntity<String> getWordsByModuleId(
    @Parameter(description = "The id of child content module", required = true, example = "19")
    @RequestParam Long childModuleId,
    @Parameter(description = "The id of user", required = true, example = "1")
    @RequestParam Long userId
  ) {
    return this.wordSvc.getWordsByModuleId(childModuleId, userId);
  }

  @Operation(
    summary = "Get Simple Words Information by Module Id for Listen-Write purpose",
    description = "Get Simple Words Information by Module Id for Listen-Write purpose"
  )
  @GetMapping(value = "/words/simple/by-child-content-module-id", produces = "application/json")
  public ResponseEntity<String> getWordsSimpleByModuleId(
    @Parameter(description = "The id of child content module", required = true, example = "19")
    @RequestParam Long childModuleId
  ) {
    return this.wordSvc.getWordsSimpleByModuleId(childModuleId);
  }


}
