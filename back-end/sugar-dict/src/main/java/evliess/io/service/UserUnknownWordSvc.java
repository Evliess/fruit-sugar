package evliess.io.service;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import evliess.io.controller.Constants;
import evliess.io.entity.UserUnknownWord;
import evliess.io.entity.Word;
import evliess.io.jpa.UserUnknownWordRepo;
import evliess.io.jpa.WordRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserUnknownWordSvc {
  private final UserUnknownWordRepo userUnknownWordRepo;
  private final WordRepo wordRepo;

  @Autowired
  public UserUnknownWordSvc(UserUnknownWordRepo userUnknownWordRepo, WordRepo wordRepo) {
    this.userUnknownWordRepo = userUnknownWordRepo;
    this.wordRepo = wordRepo;
  }

  public ResponseEntity<String> markAsUnKnown(Long userId, Long wordId, Long moduleId) {
    JSONObject jsonObject = new JSONObject();
    if (userId == null || wordId == null || moduleId == null) {
      jsonObject.put(Constants.RESULT, Constants.ERROR);
      jsonObject.put(Constants.MSG, "Required Parameters Err");
      log.error("userId: {}, wordId: {}, moduleId: {}", userId, wordId, moduleId);
      return ResponseEntity.ok(jsonObject.toString());
    }
    boolean exist = this.userUnknownWordRepo.existsByUserIdAndWordId(userId, wordId);
    if (!exist) {
      UserUnknownWord userUnknownWord = new UserUnknownWord();
      userUnknownWord.setUserId(userId);
      userUnknownWord.setWordId(wordId);
      userUnknownWord.setModuleId(moduleId);
      userUnknownWord.setCreatedAt(LocalDateTime.now());
      this.userUnknownWordRepo.save(userUnknownWord);
    }
    jsonObject.put(Constants.RESULT, Constants.OK);
    return ResponseEntity.ok(jsonObject.toString());
  }

  @Transactional
  public ResponseEntity<String> remove(Long userId, Long wordId) {
    JSONObject jsonObject = new JSONObject();
    if (userId == null || wordId == null) {
      jsonObject.put(Constants.RESULT, Constants.ERROR);
      jsonObject.put(Constants.MSG, "Required Parameters Err");
      log.error("userId: {}, wordId: {}, wordId: {}", userId, wordId, wordId);
      return ResponseEntity.ok(jsonObject.toString());
    }
    this.userUnknownWordRepo.deleteUserUnknownWordsByUserIdAndWordId(userId, wordId);
    jsonObject.put(Constants.RESULT, Constants.OK);
    return ResponseEntity.ok(jsonObject.toString());
  }

  public ResponseEntity<String> getUnknownWordsByUserId(Long userId) {
    JSONObject jsonObject = new JSONObject();
    if (userId == null) {
      jsonObject.put(Constants.RESULT, Constants.ERROR);
      jsonObject.put(Constants.MSG, "Required Parameters Err");
      log.error("userId is null");
      return ResponseEntity.ok(jsonObject.toString());
    }
    List<UserUnknownWord> words = this.userUnknownWordRepo.getUserUnknownWordsByUserId(userId);
    JSONArray wordArr = new JSONArray();
    for (UserUnknownWord word : words) {
      JSONObject wordObj = buildWordObj(word);
      if (wordObj.getString("id") != null) {
        wordArr.add(wordObj);
      }
    }
    jsonObject.put(Constants.RESULT, Constants.OK);
    jsonObject.put("words", wordArr);
    jsonObject.put("count", wordArr.size());
    return ResponseEntity.ok(jsonObject.toString());
  }

  public boolean existsByUserIdAndWordId(Long userId, Long wordId) {
    return this.userUnknownWordRepo.existsByUserIdAndWordId(userId, wordId);
  }

  private JSONObject buildWordObj(UserUnknownWord unknownWord) {
    JSONObject wordObj = new JSONObject();
    Optional<Word> wordOpt = this.wordRepo.findById(unknownWord.getWordId());
    if (wordOpt.isPresent()) {
      Word word = wordOpt.get();
      wordObj.put("id", word.getId());
      wordObj.put("text", word.getText());
      wordObj.put("definition", word.getDefinition());
      wordObj.put("phoneticUS", word.getPhoneticUS());
      wordObj.put("phoneticUK", word.getPhoneticUK());
      wordObj.put("audioUSUrl", word.getAudioUSUrl());
      wordObj.put("audioUKUrl", word.getAudioUKUrl());
      wordObj.put("phrases", word.getPhrases());
      wordObj.put("sentences", word.getSentences());
    }
    return wordObj;
  }
}
