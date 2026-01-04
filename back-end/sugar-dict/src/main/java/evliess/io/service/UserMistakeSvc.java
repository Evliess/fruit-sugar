package evliess.io.service;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import evliess.io.controller.Constants;
import evliess.io.entity.Sentence;
import evliess.io.entity.UserMistake;
import evliess.io.entity.Word;
import evliess.io.jpa.SentenceRepo;
import evliess.io.jpa.UserMistakeRepo;
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
public class UserMistakeSvc {
  private final WordRepo wordRepo;
  private final SentenceRepo sentenceRepo;
  private final UserMistakeRepo mistakeRepo;

  @Autowired
  public UserMistakeSvc(WordRepo wordRepo, SentenceRepo sentenceRepo, UserMistakeRepo mistakeRepo) {
    this.wordRepo = wordRepo;
    this.sentenceRepo = sentenceRepo;
    this.mistakeRepo = mistakeRepo;
  }

  public ResponseEntity<String> markAsMistake(Long userId, Long wordId, Long moduleId) {
    JSONObject jsonObject = new JSONObject();
    if (userId == null || wordId == null || moduleId == null) {
      jsonObject.put(Constants.RESULT, Constants.ERROR);
      jsonObject.put(Constants.MSG, "Required Parameters Err");
      log.error("userId: {}, wordId: {}, moduleId: {}", userId, wordId, moduleId);
      return ResponseEntity.ok(jsonObject.toString());
    }
    boolean exist = this.mistakeRepo.existsByUserIdAndWordId(userId, wordId);
    if (!exist) {
      UserMistake userMistakeWord = new UserMistake();
      userMistakeWord.setUserId(userId);
      userMistakeWord.setWordId(wordId);
      userMistakeWord.setModuleId(moduleId);
      userMistakeWord.setCreatedAt(LocalDateTime.now());
      this.mistakeRepo.save(userMistakeWord);
    }
    jsonObject.put(Constants.RESULT, Constants.OK);
    return ResponseEntity.ok(jsonObject.toString());
  }

  public ResponseEntity<String> markAsMistakeSentence(Long userId, Long sentenceId, Long moduleId) {
    JSONObject jsonObject = new JSONObject();
    if (userId == null || sentenceId == null || moduleId == null) {
      jsonObject.put(Constants.RESULT, Constants.ERROR);
      jsonObject.put(Constants.MSG, "Required Parameters Err");
      log.error("userId: {}, sentenceId: {}, moduleId: {}", userId, sentenceId, moduleId);
      return ResponseEntity.ok(jsonObject.toString());
    }
    boolean exist = this.mistakeRepo.existsByUserIdAndSentenceId(userId, sentenceId);
    if (!exist) {
      UserMistake userMistakeSentence = new UserMistake();
      userMistakeSentence.setUserId(userId);
      userMistakeSentence.setSentenceId(sentenceId);
      userMistakeSentence.setModuleId(moduleId);
      userMistakeSentence.setCreatedAt(LocalDateTime.now());
      this.mistakeRepo.save(userMistakeSentence);
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
    this.mistakeRepo.deleteUserMistakeWordByUserIdAndWordId(userId, wordId);
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
    this.mistakeRepo.deleteUserMistakeSentenceByUserIdAndSentenceId(userId, sentenceId);
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
    List<UserMistake> words = this.mistakeRepo.getUserMistakeWordsByUserId(userId);
    JSONArray wordArr = new JSONArray();
    for (UserMistake word : words) {
      JSONObject wordObj = buildWordObj(word);
      if (wordObj.getString("id") != null) {
        wordArr.add(wordObj);
      }
    }
    List<UserMistake> sentences = this.mistakeRepo.getUserMistakeSentencesByUserId(userId);
    for (UserMistake sentence : sentences) {
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

  private JSONObject buildSentenceObj(UserMistake userMistake) {
    JSONObject sentenceObj = new JSONObject();
    Optional<Sentence> sentenceOpt = this.sentenceRepo.findById(userMistake.getSentenceId());
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

  private JSONObject buildWordObj(UserMistake userMistake) {
    JSONObject wordObj = new JSONObject();
    Optional<Word> wordOpt = this.wordRepo.findById(userMistake.getWordId());
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
