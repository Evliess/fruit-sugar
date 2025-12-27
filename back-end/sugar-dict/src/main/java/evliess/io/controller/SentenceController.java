package evliess.io.controller;

import evliess.io.service.SentenceSvc;
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
@Tag(name = "Sentence Operations", description = "Sentence Operations")
public class SentenceController {

  private final SentenceSvc sentenceSvc;

  @Autowired
  public SentenceController(SentenceSvc sentenceSvc) {
    this.sentenceSvc = sentenceSvc;
  }

  @GetMapping(value = "/sentences/by-content-module-id", produces = "application/json")
  public ResponseEntity<String> getSentencesByModuleId(
    @Parameter(description = "The id of content module", required = true, example = "11")
    @RequestParam Long moduleId
  ) {
    return this.sentenceSvc.getSentencesByModuleId(moduleId);
  }
}
