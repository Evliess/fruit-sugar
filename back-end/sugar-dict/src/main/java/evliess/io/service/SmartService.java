package evliess.io.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import evliess.io.dto.SmartRequestDto;
import evliess.io.util.RestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SmartService {
  private static final Logger logger = LoggerFactory.getLogger(SmartService.class);
  private static final String DEEPSEEK_API_KEY = System.getenv("DP_SECRET");

  public String getSmartWord(SmartRequestDto request) {
    if (request.getContent() == null || request.getContent().trim().isEmpty()) {
      throw new IllegalArgumentException("Content is required");
    }

    logger.info("Processing word request from user {}: {}", request.getUserId(), request.getContent());
    try {
      String response = RestUtils.dpskSmartChat(request.getContent(), DEEPSEEK_API_KEY);

      if (response == null) {
        throw new RuntimeException("Failed to get AI response");
      }

      logger.info("AI response for word: {}", response);
      return response;
    } catch (JsonProcessingException e) {
      logger.error("Error processing word request for user {}", request.getUserId(), e);
      throw new RuntimeException("Error processing word request", e);
    }
  }

  public String getSmartSentence(SmartRequestDto request) {
    if (request.getContent() == null || request.getContent().trim().isEmpty()) {
      throw new IllegalArgumentException("Content is required");
    }

    logger.info("Processing sentence request from user {}: {}", request.getUserId(), request.getContent());
    try {
      String response = RestUtils.dpskSmartChat(request.getContent(), DEEPSEEK_API_KEY);

      if (response == null) {
        throw new RuntimeException("Failed to get AI response");
      }

      logger.info("AI response for sentence: {}", response);
      return response;
    } catch (JsonProcessingException e) {
      logger.error("Error processing sentence request for user {}", request.getUserId(), e);
      throw new RuntimeException("Error processing sentence request", e);
    }
  }
}
