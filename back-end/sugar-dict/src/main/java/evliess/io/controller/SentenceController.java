package evliess.io.controller;

import evliess.io.service.ContentModuleSvc;
import evliess.io.service.SentenceSvc;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/private/v1")
@Tag(name = "Sentence Operations", description = "Sentence Operations")
public class SentenceController {

  private final SentenceSvc sentenceSvc;
  private final ContentModuleSvc contentModuleSvc;

  @Autowired
  public SentenceController(SentenceSvc sentenceSvc, ContentModuleSvc contentModuleSvc) {
    this.sentenceSvc = sentenceSvc;
    this.contentModuleSvc = contentModuleSvc;
  }

  @Operation(
    summary = "Get Sentence Information by Module Id",
    description = "Get Sentence Information by Module Id"
  )
  @GetMapping(value = "/sentences/by-content-module-id", produces = "application/json")
  public ResponseEntity<String> getSentencesByModuleId(
    @Parameter(description = "The id of content module", required = true, example = "1")
    @RequestParam Long moduleId,
    @Parameter(description = "The id of user", required = true, example = "1")
    @RequestParam Long userId
  ) {
    return this.sentenceSvc.getSentencesByModuleId(moduleId, userId);
  }

  @Operation(summary = "Get all sentences content modules", description = "Get all sentences modules")
  @GetMapping("/sentences/content-modules/{userId}")
  public ResponseEntity<String> getSentencesContentModules(
    @Parameter(description = "userId", required = true, example = "1")
    @PathVariable Long userId
  ) {
    return contentModuleSvc.getAllContentModules(userId);
  }
}
