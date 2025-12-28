package evliess.io.service;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import evliess.io.controller.Constants;
import evliess.io.entity.Word;
import evliess.io.jpa.WordRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class WordSvc {
  private final WordRepo wordRepo;

  @Autowired
  public WordSvc(WordRepo wordRepo) {
    this.wordRepo = wordRepo;
  }

  public ResponseEntity<String> getWordsSimpleByModuleId(Long childModuleId) {
    return buildResponseEntity(childModuleId, true);
  }

  public ResponseEntity<String> getWordsByModuleId(Long childModuleId) {
    return buildResponseEntity(childModuleId, false);
  }

  private ResponseEntity<String> buildResponseEntity(Long childModuleId, boolean isSimple) {
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
      JSONArray wordArr = buildWordsArray(words, isSimple);
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

  private static JSONArray buildWordsArray(List<Word> words, boolean isSimple) {
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
        wordObj.put("isKnown", false);
      }
      wordArr.add(wordObj);
    }
    return wordArr;
  }
}
