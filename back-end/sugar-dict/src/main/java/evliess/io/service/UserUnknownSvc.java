package evliess.io.service;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import evliess.io.controller.Constants;
import evliess.io.entity.Sentence;
import evliess.io.entity.UserUnknown;
import evliess.io.entity.Word;
import evliess.io.jpa.SentenceRepo;
import evliess.io.jpa.UserUnknownRepo;
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
public class UserUnknownSvc {
  private final UserUnknownRepo userUnknownWordRepo;
  private final WordRepo wordRepo;
  private final SentenceRepo sentenceRepo;

  @Autowired
  public UserUnknownSvc(UserUnknownRepo userUnknownWordRepo
    , WordRepo wordRepo
    , SentenceRepo sentenceRepo) {
    this.userUnknownWordRepo = userUnknownWordRepo;
    this.wordRepo = wordRepo;
    this.sentenceRepo = sentenceRepo;
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
      UserUnknown userUnknownWord = new UserUnknown();
      userUnknownWord.setUserId(userId);
      userUnknownWord.setWordId(wordId);
      userUnknownWord.setModuleId(moduleId);
      userUnknownWord.setCreatedAt(LocalDateTime.now());
      this.userUnknownWordRepo.save(userUnknownWord);
    }
    jsonObject.put(Constants.RESULT, Constants.OK);
    return ResponseEntity.ok(jsonObject.toString());
  }

  public ResponseEntity<String> markAsUnKnownSentence(Long userId, Long sentenceId, Long moduleId) {
    JSONObject jsonObject = new JSONObject();
    if (userId == null || sentenceId == null || moduleId == null) {
      jsonObject.put(Constants.RESULT, Constants.ERROR);
      jsonObject.put(Constants.MSG, "Required Parameters Err");
      log.error("userId: {}, sentenceId: {}, moduleId: {}", userId, sentenceId, moduleId);
      return ResponseEntity.ok(jsonObject.toString());
    }
    boolean exist = this.userUnknownWordRepo.existsByUserIdAndSentenceId(userId, sentenceId);
    if (!exist) {
      UserUnknown userUnknownWord = new UserUnknown();
      userUnknownWord.setUserId(userId);
      userUnknownWord.setSentenceId(sentenceId);
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
    this.userUnknownWordRepo.deleteUserUnknownWordByUserIdAndWordId(userId, wordId);
    jsonObject.put(Constants.RESULT, Constants.OK);
    return ResponseEntity.ok(jsonObject.toString());
  }

  @Transactional
  public ResponseEntity<String> removeSentence(Long userId, Long sentenceId) {
    JSONObject jsonObject = new JSONObject();
    if (userId == null || sentenceId == null) {
      jsonObject.put(Constants.RESULT, Constants.ERROR);
      jsonObject.put(Constants.MSG, "Required Parameters Err");
      log.error("userId: {}, wordId: {}", userId, sentenceId);
      return ResponseEntity.ok(jsonObject.toString());
    }
    this.userUnknownWordRepo.deleteUserUnknownSentenceByUserIdAndWordId(userId, sentenceId);
    jsonObject.put(Constants.RESULT, Constants.OK);
    return ResponseEntity.ok(jsonObject.toString());
  }

  public ResponseEntity<String> getUnknownByUserId(Long userId) {
    JSONObject jsonObject = new JSONObject();
    if (userId == null) {
      jsonObject.put(Constants.RESULT, Constants.ERROR);
      jsonObject.put(Constants.MSG, "Required Parameters Err");
      log.error("userId is null");
      return ResponseEntity.ok(jsonObject.toString());
    }
    List<UserUnknown> words = this.userUnknownWordRepo.getUserUnknownWordsByUserId(userId);
    JSONArray wordArr = new JSONArray();
    for (UserUnknown word : words) {
      JSONObject wordObj = buildWordObj(word);
      if (wordObj.getString("id") != null) {
        wordArr.add(wordObj);
      }
    }
    List<UserUnknown> sentences = this.userUnknownWordRepo.getUserUnknownSentencesByUserId(userId);
    for (UserUnknown sentence : sentences) {
      JSONObject sentenceObj = buildSentenceObj(sentence);
      if (sentenceObj.getString("id") != null) {
        wordArr.add(sentenceObj);
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

  private JSONObject buildSentenceObj(UserUnknown unknownWord) {
    JSONObject sentenceObj = new JSONObject();
    Optional<Sentence> sentenceOpt = this.sentenceRepo.findById(unknownWord.getSentenceId());
    if (sentenceOpt.isPresent()) {
      Sentence sentence = sentenceOpt.get();
      sentenceObj.put("id", sentence.getId());
      sentenceObj.put("text", sentence.getText());
      JSONObject definitionObj = new JSONObject();
      definitionObj.put("textTranslation", sentence.getTextTranslation());
      sentenceObj.put("definition", definitionObj.toString());
      sentenceObj.put("audioUSUrl", sentence.getAudioUSUrl());
      sentenceObj.put("audioUKUrl", sentence.getAudioUKUrl());
      sentenceObj.put("type", Constants.UNKNOWN_TYPE_SENTENCE);
    }
    return sentenceObj;
  }

  private JSONObject buildWordObj(UserUnknown unknownWord) {
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
      wordObj.put("type", Constants.UNKNOWN_TYPE_WORD);
    }
    return wordObj;
  }
}
