package evliess.io.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonUtils {

  public static String toJsonString(Object obj) {
    if (obj == null) {
      return "null";
    }
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      return objectMapper.writeValueAsString(obj);
    } catch (JsonProcessingException e) {
      log.error("JsonUtils toJsonString error: {}", e.getMessage());
      return e.getMessage();
    }
  }
}
