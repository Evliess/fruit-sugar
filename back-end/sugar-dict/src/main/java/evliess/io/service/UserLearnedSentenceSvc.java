package evliess.io.service;

import com.alibaba.fastjson2.JSONObject;
import evliess.io.controller.Constants;
import evliess.io.entity.UserLearnedSentence;
import evliess.io.entity.UserSentenceProgress;
import evliess.io.jpa.SentenceRepo;
import evliess.io.jpa.UserLearnedSentenceRepo;
import evliess.io.jpa.UserSentenceProgressRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
public class UserLearnedSentenceSvc {
  private final UserUnknownSvc userUnknownSvc;
  private final UserLearnedSentenceRepo userLearnedSentenceRepo;
  private final UserSentenceProgressRepo userSentenceProgressRepo;
  private final SentenceRepo sentenceRepo;

  public UserLearnedSentenceSvc(UserUnknownSvc userUnknownSvc
    , UserLearnedSentenceRepo userLearnedSentenceRepo
    , UserSentenceProgressRepo userSentenceProgressRepo
    , SentenceRepo sentenceRepo) {
    this.userUnknownSvc = userUnknownSvc;
    this.userLearnedSentenceRepo = userLearnedSentenceRepo;
    this.userSentenceProgressRepo = userSentenceProgressRepo;
    this.sentenceRepo = sentenceRepo;
  }

  @Transactional
  public ResponseEntity<String> markAsUnKnown(Long userId, Long sentenceId, Long moduleId) {
    JSONObject jsonObject = new JSONObject();
    if (userId == null || sentenceId == null) {
      jsonObject.put(Constants.RESULT, Constants.ERROR);
      jsonObject.put(Constants.MSG, "Required Parameters Err");
      log.error("userId: {}, wordId: {},", userId, sentenceId);
      return ResponseEntity.ok(jsonObject.toString());
    }
    boolean exist = this.userLearnedSentenceRepo.existsByUserIdAndSentenceId(userId, sentenceId);
    if (exist) {
      this.userLearnedSentenceRepo.deleteByUserIdAndSentenceId(userId, sentenceId);
      this.userSentenceProgressRepo.decrementLearnedCount(userId, moduleId);
    }
    this.userUnknownSvc.markAsUnKnownSentence(userId, sentenceId, moduleId);
    jsonObject.put(Constants.RESULT, Constants.OK);
    return ResponseEntity.ok(jsonObject.toString());
  }

  @Transactional
  public ResponseEntity<String> markAsKnown(Long userId, Long sentenceId, Long moduleId) {
    JSONObject jsonObject = new JSONObject();
    if (userId == null || sentenceId == null || moduleId == null) {
      jsonObject.put(Constants.RESULT, Constants.ERROR);
      jsonObject.put(Constants.MSG, "Required Parameters Err");
      log.error("userId: {}, wordId: {}, moduleId: {}", userId, sentenceId, moduleId);
      return ResponseEntity.ok(jsonObject.toString());
    }
    boolean exist = this.userLearnedSentenceRepo.existsByUserIdAndSentenceId(userId, sentenceId);
    if (!exist) {
      UserLearnedSentence userLearnedSentence = new UserLearnedSentence();
      userLearnedSentence.setLearnedAt(LocalDateTime.now());
      userLearnedSentence.setUserId(userId);
      userLearnedSentence.setSentenceId(sentenceId);
      userLearnedSentence.setModuleId(moduleId);
      this.userLearnedSentenceRepo.save(userLearnedSentence);
      this.createOrIncrementLearnedCounts(userId, moduleId, 1);
    }
    if (this.userUnknownSvc.existsByUserIdAndSentenceId(userId, sentenceId)) {
      this.userUnknownSvc.remove(userId, sentenceId);
    }
    jsonObject.put(Constants.RESULT, Constants.OK);
    return ResponseEntity.ok(jsonObject.toString());
  }

  private void createOrIncrementLearnedCounts(Long userId, Long moduleId, int learnedWordCounts) {
    UserSentenceProgress progress = userSentenceProgressRepo
      .findByUserIdAndModuleId(userId, moduleId);
    if (progress != null) {
      userSentenceProgressRepo.incrementLearnedCount(userId, moduleId);
    } else if (moduleId != null && learnedWordCounts > 0) {
      UserSentenceProgress newProgress = new UserSentenceProgress();
      newProgress.setUserId(userId);
      newProgress.setModuleId(moduleId);
      newProgress.setTotalSentencesCount(this.sentenceRepo.countSentencesByModuleId(moduleId));
      newProgress.setLearnedSentencesCount(learnedWordCounts);
      newProgress.setLastStudiedAt(LocalDateTime.now());
      userSentenceProgressRepo.save(newProgress);
    }
  }
}
