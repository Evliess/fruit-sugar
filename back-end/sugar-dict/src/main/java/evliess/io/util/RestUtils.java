package evliess.io.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import evliess.io.controller.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class RestUtils {
  private static final String SORRY_MESSAGE = "Ops, Something is wrong with dpsk service!";
  private static final Double TEMPERATURE = 1.0;
  private static final Logger logger = LoggerFactory.getLogger(RestUtils.class);
  private static final ReentrantLock lock = new ReentrantLock();

  private static RestTemplate buildRestTemplate() {
    return new RestTemplateBuilder().connectTimeout(Duration.ofMinutes(2L))
      .readTimeout(Duration.ofMinutes(2L)).build();
  }

  public static String dpskChat(String msg, String token) throws JsonProcessingException {
    String uuid = UUID.randomUUID().toString();
    logger.info("DP is answering: {}", uuid);
    String resp = postChat(msg, token, Constants.DPSK_MODEL, Constants.DPSK_CHAT_URL, uuid);
    if (resp == null) {
      logger.error("Failed to chat with DP");
    } else {
      logger.info("DP response: {} - {}", uuid, resp);
    }
    return resp;
  }

  public static String dpskSentenceChat(String sentence, String token) throws JsonProcessingException {
    String uuid = UUID.randomUUID().toString();
    logger.info("DP is answering sentence: {}", uuid);
    String resp = postSentenceChat(sentence, token, Constants.DPSK_MODEL, Constants.DPSK_CHAT_URL, uuid);
    if (resp == null) {
      logger.error("Failed to chat with DP for sentence");
    } else {
      logger.info("DP sentence response: {} - {}", uuid, resp);
    }
    return resp;
  }

  private static String postSentenceChat(String sentence, String token, String model, String url, String uuid) throws JsonProcessingException {
    RestTemplate restTemplate = buildRestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Type", "application/json");
    headers.add("Authorization", "Bearer " + token);
    HttpEntity<String> request = getStringHttpEntitySentence(uuid, model, headers, sentence);
    try {
      ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
      if (response.getStatusCode() == HttpStatus.OK) {
        String respBody = response.getBody();
        return convertOpenAIJsonResponse(respBody);
      } else {
        return null;
      }
    } catch (Exception e) {
      logger.error("{}:{}", uuid, e.getMessage(), e);
      return null;
    }
  }

  private static String postChat(String msg, String token, String model, String url, String uuid) throws JsonProcessingException {
    RestTemplate restTemplate = buildRestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Type", "application/json");
    headers.add("Authorization", "Bearer " + token);
    HttpEntity<String> request = getStringHttpEntity(uuid, model, headers, msg);
    try {
      ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
      if (response.getStatusCode() == HttpStatus.OK) {
        String respBody = response.getBody();
        return convertOpenAIJsonResponse(respBody);
      } else {
        return null;
      }
    } catch (Exception e) {
      logger.error("{}:{}", uuid, e.getMessage(), e);
      return null;
    }
  }

  private static String convertOpenAIJsonResponse(String response) {
    if (response == null) {
      return SORRY_MESSAGE;
    }
    JSONObject jsonObject = JSON.parseObject(response);
    JSONArray choices = jsonObject.getJSONArray("choices");
    if (choices != null && !choices.isEmpty()) {
      JSONObject choice = (JSONObject) choices.get(0);
      JSONObject message = (JSONObject) choice.get("message");
      return message.getString("content");
    }
    return SORRY_MESSAGE;
  }

  private static HttpEntity<String> getStringHttpEntitySentence(String uuid, String model, HttpHeaders headers, String sentence)
    throws JsonProcessingException {
    return buildChatHttpEntity(headers, model, Constants.SENTENCE_SYS_PROMPT, sentence);
  }

  private static HttpEntity<String> getStringHttpEntity(String uuid, String model, HttpHeaders headers, String word)
    throws JsonProcessingException {
    return buildChatHttpEntity(headers, model, Constants.WORD_SYS_PROMPT, word);
  }

  private static HttpEntity<String> buildChatHttpEntity(HttpHeaders headers, String model, String systemPrompt, String userContent)
    throws JsonProcessingException {
    Map<String, Object> body = new HashMap<>();
    List<Map<String, String>> messages = new ArrayList<>();
    messages.add(Map.of("role", "system", "content", systemPrompt));
    messages.add(Map.of("role", "user", "content", userContent));
    body.put("messages", messages);
    body.put("model", model);
    body.put("stream", false);
    body.put("temperature", TEMPERATURE);
    body.put("response_format", Map.of("type", "json_object"));
    return new HttpEntity<>(new ObjectMapper().writeValueAsString(body), headers);
  }

  public static String getSentenceTTS(String APP_KEY, String APP_SECRET, String voiceName, String sentence, File audioDir) throws Exception {
    Map<String, String> params = new HashMap<>();
    String salt = String.valueOf(System.currentTimeMillis());
    params.put("voiceName", voiceName);
    String signStr = APP_KEY + sentence + salt + APP_SECRET;
    String sign = getDigest(signStr);
    params.put("appKey", APP_KEY);
    params.put("q", sentence);
    params.put("salt", salt);
    params.put("sign", sign);
    params.put("speed", "1.0");
    params.put("volume", "1.00");
    return requestForPost(Constants.YD_TTS_URL, params, audioDir);
  }

  public static String requestForPost(String url, Map<String, String> params, File audioDir) throws IOException {
    RestTemplate restTemplate = new RestTemplateBuilder().connectTimeout(Duration.ofMinutes(2L))
      .readTimeout(Duration.ofMinutes(2L)).build();
    MultiValueMap<String, String> formParams = new LinkedMultiValueMap<>();
    params.forEach(formParams::add);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(formParams, headers);
    String filePath = null;
    try {
      ResponseEntity<byte[]> response = restTemplate.exchange(
        url, HttpMethod.POST, requestEntity, byte[].class
      );
      String contentType = Objects.requireNonNull(response.getHeaders().getContentType()).toString();
      byte[] body = response.getBody();
      if (body == null) {
        logger.warn("响应体为空");
        return null;
      }
      if (contentType.startsWith("audio/mp3")) {
        String sentence = params.get("q");
        String voiceName = params.get("voiceName");
        if (Constants.VOICE_BR.equals(voiceName)) {
          filePath = audioDir.getAbsolutePath() + File.separator + getDigest(sentence) + "_uk.mp3";
        } else {
          filePath = audioDir.getAbsolutePath() + File.separator + getDigest(sentence) + "_us.mp3";
        }
        lock.lock();
        Files.write(Paths.get(filePath), body);
        lock.unlock();
        logger.info("{} 已保存至: {}", sentence, filePath);
      } else {
        String json = new String(body, StandardCharsets.UTF_8);
        logger.info(json);
      }
    } catch (Exception e) {
      logger.error("请求失败", e);
      throw new IOException("HTTP 请求异常", e);
    }
    return filePath;
  }

  public static String getBase64(String string) {
    if (string == null) {
      return null;
    }
    return Base64.getEncoder().encodeToString(string.getBytes(StandardCharsets.UTF_8));
  }

  public static String getDigest(String string) {
    if (string == null) {
      return null;
    }
    char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    byte[] btInput = string.getBytes(StandardCharsets.UTF_8);
    try {
      MessageDigest mdInst = MessageDigest.getInstance("MD5");
      mdInst.update(btInput);
      byte[] md = mdInst.digest();
      int j = md.length;
      char[] str = new char[j * 2];
      int k = 0;
      for (byte byte0 : md) {
        str[k++] = hexDigits[byte0 >>> 4 & 0xf];
        str[k++] = hexDigits[byte0 & 0xf];
      }
      return new String(str);
    } catch (NoSuchAlgorithmException e) {
      return null;
    }
  }
}
