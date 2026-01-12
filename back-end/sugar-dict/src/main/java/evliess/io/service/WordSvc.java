package evliess.io.service;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import evliess.io.controller.Constants;
import evliess.io.entity.Word;
import evliess.io.jpa.UserLearnedWordRepo;
import evliess.io.jpa.WordRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
public class WordSvc {
  private final WordRepo wordRepo;
  private final UserLearnedWordRepo userLearnedWordRepo;
  private final DaoYouSvc daoYouSvc;

  @Autowired
  public WordSvc(WordRepo wordRepo, UserLearnedWordRepo userLearnedWordRepo, DaoYouSvc daoYouSvc) {
    this.wordRepo = wordRepo;
    this.userLearnedWordRepo = userLearnedWordRepo;
    this.daoYouSvc = daoYouSvc;
  }

  public ResponseEntity<String> getWordsSimpleByModuleId(Long childModuleId) {
    return buildResponseEntity(childModuleId, true, null);
  }

  public ResponseEntity<String> getWordsByModuleId(Long childModuleId, Long userId) {
    return buildResponseEntity(childModuleId, false, userId);
  }

  private ResponseEntity<String> buildResponseEntity(Long childModuleId, boolean isSimple, Long userId) {
    JSONObject jsonObject = new JSONObject();
    if (childModuleId == null) {
      jsonObject.put(Constants.RESULT, Constants.ERROR);
      jsonObject.put("words", new JSONArray());
      jsonObject.put("count", 0);
      log.error("module id is null.");
      return ResponseEntity.ok(jsonObject.toString());
    }
    try {
      List<Word> words = this.wordRepo.getWordsByModuleId(childModuleId);
      if (words == null || words.isEmpty()) {
        jsonObject.put(Constants.RESULT, Constants.ERROR);
        jsonObject.put("count", 0);
        jsonObject.put("words", new JSONArray());
        log.error("No words found for module ID: {}", childModuleId);
        return ResponseEntity.ok(jsonObject.toString());
      }
      List<Long> knownWords = new ArrayList<>();
      if (userId != null) {
        knownWords = this.userLearnedWordRepo.findLearnedWordIds(userId, childModuleId);
      }
      JSONArray wordArr = buildWordsArray(words, knownWords, isSimple);
      jsonObject.put(Constants.RESULT, Constants.OK);
      jsonObject.put("words", wordArr);
      jsonObject.put("count", wordArr.size());
      return ResponseEntity.ok(jsonObject.toString());
    } catch (Exception e) {
      jsonObject.put(Constants.RESULT, Constants.ERROR);
      jsonObject.put("count", 0);
      jsonObject.put("words", new JSONArray());
      log.error("Error retrieving words for module ID: {}", childModuleId, e);
      return ResponseEntity.ok(jsonObject.toString());
    }
  }

  public static JSONArray buildWordsArray(List<Word> words, List<Long> knownWords, boolean isSimple) {
    JSONArray wordArr = new JSONArray();
    for (Word word : words) {
      JSONObject wordObj = new JSONObject();
      wordObj.put("id", word.getId());
      wordObj.put("text", word.getText());
      wordObj.put("definition", word.getDefinition());
      if (!isSimple) {
        wordObj.put("phoneticUS", word.getPhoneticUS());
        wordObj.put("phoneticUK", word.getPhoneticUK());
        wordObj.put("audioUSUrl", word.getAudioUSUrl());
        wordObj.put("audioUKUrl", word.getAudioUKUrl());
        wordObj.put("phrases", word.getPhrases());
        wordObj.put("sentences", word.getSentences());
        wordObj.put("moduleId", word.getModuleId());
        wordObj.put("isKnown", knownWords.contains(word.getId()));
      }
      wordArr.add(wordObj);
    }
    return wordArr;
  }

  @Transactional
  public ResponseEntity<String> getAllWords() {
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("result", "ok");
    Long start = 2511L;
    Long end = 5542L;
    List<Word> words = this.wordRepo.getByIDRange(start, end);
    StringBuilder err = new StringBuilder();
    for (Word word : words) {
      try {
        String text = word.getText();
        String digest = this.daoYouSvc.getTextVoiceSpecifyDir(text, "words", 200);
        this.wordRepo.updateAudioUrlBase64(digest + "_us.mp3", digest + "_uk.mp3", word.getId());
        log.info("current word is done: {}, {}", word.getId(), word.getText());
        int random = ThreadLocalRandom.current().nextInt(1000, 1200);
        Thread.sleep(random);
      } catch (Exception e) {
        log.error("word id: {}", word.getId(), e);
        err.append(word.getId()).append("[^]").append(word.getText()).append("\n");
      }
    }

    try (java.io.FileWriter writer =
           new java.io.FileWriter("D:\\error\\" + start + "-" + end + ".txt")) {
      writer.write(err.toString());
    } catch (Exception e) {
      log.error("Error writing to file", e);
    }
    return ResponseEntity.ok(jsonObject.toString());
  }
}
