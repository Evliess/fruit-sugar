package evliess.io.controller;

import evliess.io.dto.SmartRequestDto;
import evliess.io.service.SmartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.function.Function;

@Controller
@RequestMapping("/private/v1/ai")
@Tag(name = "AI Smart Operations", description = "AI-powered smart word and sentence analysis operations")
public class SmartController {
  private static final Logger logger = LoggerFactory.getLogger(SmartController.class);
  private final SmartService smartService;

  public SmartController(SmartService smartService) {
    this.smartService = smartService;
  }

  @Operation(
    summary = "Get smart word analysis",
    description = "Analyze a word or phrase using AI to get detailed information including pronunciation, collocations, and example sentences"
  )
  @PostMapping("/word/smart")
  public ResponseEntity<String> getSmartWord(
    @Parameter(description = "Request body containing content and userId", required = true)
    @RequestBody SmartRequestDto request) {
    return handleSmartRequest(request, smartService::getSmartWord, "word");
  }

  @Operation(
    summary = "Get smart sentence analysis",
    description = "Analyze a sentence using AI to get translation, alternative expressions, and language notes"
  )
  @PostMapping("/sentence/smart")
  public ResponseEntity<String> getSmartSentence(
    @Parameter(description = "Request body containing content and userId", required = true)
    @RequestBody SmartRequestDto request) {
    return handleSmartRequest(request, smartService::getSmartSentence, "sentence");
  }

  private ResponseEntity<String> handleSmartRequest(
    SmartRequestDto request,
    Function<SmartRequestDto, String> serviceMethod,
    String requestType) {
    try {
      String response = serviceMethod.apply(request);
      return ResponseEntity.ok(response);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (RuntimeException e) {
      logger.error("Error getting AI response for {}", requestType, e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(e.getMessage());
    }
  }
}
