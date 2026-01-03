package evliess.io.service;

import com.alibaba.fastjson2.JSONObject;
import evliess.io.controller.Constants;
import evliess.io.entity.UserLearnedWord;
import evliess.io.entity.UserWordProgress;
import evliess.io.jpa.UserLearnedWordRepo;
import evliess.io.jpa.UserWordProgressRepo;
import evliess.io.jpa.WordRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
public class UserLearnedWordSvc {
  private final UserLearnedWordRepo userLearnedWordRepo;
  private final UserUnknownWordSvc userUnknownWordSvc;
  private final UserWordProgressRepo userWordProgressRepo;
  private final WordRepo wordRepo;

  @Autowired
  public UserLearnedWordSvc(UserLearnedWordRepo userLearnedWordRepo
    , UserWordProgressRepo userWordProgressRepo, WordRepo wordRepo
    , UserUnknownWordSvc userUnknownWordSvc) {
    this.userLearnedWordRepo = userLearnedWordRepo;
    this.userWordProgressRepo = userWordProgressRepo;
    this.wordRepo = wordRepo;
    this.userUnknownWordSvc = userUnknownWordSvc;
  }

  @Transactional
  public ResponseEntity<String> markAsUnKnown(Long userId, Long wordId, Long moduleId) {
    JSONObject jsonObject = new JSONObject();
    if (userId == null || wordId == null) {
      jsonObject.put(Constants.RESULT, Constants.ERROR);
      jsonObject.put(Constants.MSG, "Required Parameters Err");
      log.error("userId: {}, wordId: {},", userId, wordId);
      return ResponseEntity.ok(jsonObject.toString());
    }
    boolean exist = this.userLearnedWordRepo.existsByUserIdAndWordId(userId, wordId);
    if (exist) {
      this.userLearnedWordRepo.deleteByUserIdAndWordId(userId, wordId);
      this.userWordProgressRepo.decrementLearnedCount(userId, moduleId);
    }
    this.userUnknownWordSvc.markAsUnKnown(userId, wordId, moduleId);
    jsonObject.put(Constants.RESULT, Constants.OK);
    return ResponseEntity.ok(jsonObject.toString());
  }

  @Transactional
  public ResponseEntity<String> markAsKnown(Long userId, Long wordId, Long moduleId) {
    JSONObject jsonObject = new JSONObject();
    if (userId == null || wordId == null || moduleId == null) {
      jsonObject.put(Constants.RESULT, Constants.ERROR);
      jsonObject.put(Constants.MSG, "Required Parameters Err");
      log.error("userId: {}, wordId: {}, moduleId: {}", userId, wordId, moduleId);
      return ResponseEntity.ok(jsonObject.toString());
    }
    boolean exist = this.userLearnedWordRepo.existsByUserIdAndWordId(userId, wordId);
    if (!exist) {
      UserLearnedWord userLearnedWord = new UserLearnedWord();
      userLearnedWord.setLearnedAt(LocalDateTime.now());
      userLearnedWord.setUserId(userId);
      userLearnedWord.setWordId(wordId);
      userLearnedWord.setModuleId(moduleId);
      this.userLearnedWordRepo.save(userLearnedWord);
      this.createOrIncrementLearnedCounts(userId, moduleId, 1);
    }
    if (this.userUnknownWordSvc.existsByUserIdAndWordId(userId, wordId)) {
      this.userUnknownWordSvc.remove(userId, wordId);
    }
    jsonObject.put(Constants.RESULT, Constants.OK);
    return ResponseEntity.ok(jsonObject.toString());
  }


  private void createOrIncrementLearnedCounts(Long userId, Long moduleId, int learnedWordCounts) {
    UserWordProgress progress = userWordProgressRepo
      .findByUserIdAndModuleId(userId, moduleId);
    if (progress != null) {
      userWordProgressRepo.incrementLearnedCount(userId, moduleId);
    } else if (moduleId != null && learnedWordCounts > 0) {
      UserWordProgress newProgress = new UserWordProgress();
      newProgress.setUserId(userId);
      newProgress.setModuleId(moduleId);
      newProgress.setTotalWordsCount(this.wordRepo.countWordsByModuleId(moduleId));
      newProgress.setLearnedWordsCount(learnedWordCounts);
      newProgress.setLastStudiedAt(LocalDateTime.now());
      userWordProgressRepo.save(newProgress);
    }
  }
}
