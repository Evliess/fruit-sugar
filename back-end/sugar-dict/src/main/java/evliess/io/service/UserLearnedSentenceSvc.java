package evliess.io.service;

import com.alibaba.fastjson2.JSONObject;
import evliess.io.controller.Constants;
import evliess.io.jpa.UserLearnedSentenceRepo;
import evliess.io.jpa.UserSentenceProgressRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class UserLearnedSentenceSvc {
  private final UserUnknownSvc userUnknownSvc;
  private final UserLearnedSentenceRepo userLearnedSentenceRepo;
  private final UserSentenceProgressRepo userSentenceProgressRepo;

  public UserLearnedSentenceSvc(UserUnknownSvc userUnknownSvc
    , UserLearnedSentenceRepo userLearnedSentenceRepo
    , UserSentenceProgressRepo userSentenceProgressRepo) {
    this.userUnknownSvc = userUnknownSvc;
    this.userLearnedSentenceRepo = userLearnedSentenceRepo;
    this.userSentenceProgressRepo = userSentenceProgressRepo;
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
}
