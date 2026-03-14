package evliess.io.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import evliess.io.controller.Result;
import evliess.io.dto.SmartRequestDto;
import evliess.io.util.RestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SmartService {
  private static final Logger logger = LoggerFactory.getLogger(SmartService.class);
  private static final String DEEPSEEK_API_KEY = System.getenv("DP_SECRET");

  public Result<String> getSmartWord(SmartRequestDto request) {
    if (request.getContent() == null || request.getContent().trim().isEmpty()) {
      return Result.error("Failed to get AI response", 500);
    }
    logger.info("Processing word request from user {}: {}", request.getUserId(), request.getContent());
    try {
      String response = RestUtils.dpskSmartChat(request.getContent(), DEEPSEEK_API_KEY);
      if (response == null) {
        return Result.error("Failed to get AI response", 500);
      }
      logger.info("AI response for word: {}", response);
      return Result.success(response, "Success");
    } catch (JsonProcessingException e) {
      logger.error("Error processing word request for user {}", request.getUserId(), e);
      return Result.error("Failed to get AI response", 500);
    }
  }

  public Result<String> getSmartSentence(SmartRequestDto request) {
    if (request.getContent() == null || request.getContent().trim().isEmpty()) {
      throw new IllegalArgumentException("Content is required");
    }
    logger.info("Processing sentence request from user {}: {}", request.getUserId(), request.getContent());
    try {
      String response = RestUtils.dpskSmartChat(request.getContent(), DEEPSEEK_API_KEY);
      if (response == null) {
        return Result.error("Failed to get AI response", 500);
      }

      logger.info("AI response for sentence: {}", response);
      return Result.success(response, "Success");
    } catch (JsonProcessingException e) {
      logger.error("Error processing sentence request for user {}", request.getUserId(), e);
      return Result.error("Failed to get AI response", 500);
    }
  }
}
