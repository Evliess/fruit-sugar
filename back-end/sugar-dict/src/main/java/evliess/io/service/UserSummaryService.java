package evliess.io.service;

import com.alibaba.fastjson2.JSONObject;
import evliess.io.controller.Constants;
import evliess.io.jpa.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserSummaryService {

  private final UserLearnedSentenceRepo userLearnedSentenceRepo;
  private final UserLearnedWordRepo userLearnedWordRepo;
  private final UserUnknownRepo userUnknownRepo;
  private final UserCustomBookRepo userCustomBookRepo;
  private final UserMistakeRepo userMistakeRepo;

  @Autowired
  public UserSummaryService(UserLearnedSentenceRepo userLearnedSentenceRepo
    , UserLearnedWordRepo userLearnedWordRepo
    , UserUnknownRepo userUnknownRepo
    , UserCustomBookRepo userCustomBookRepo
    , UserMistakeRepo userMistakeRepo
  ) {
    this.userLearnedSentenceRepo = userLearnedSentenceRepo;
    this.userLearnedWordRepo = userLearnedWordRepo;
    this.userUnknownRepo = userUnknownRepo;
    this.userCustomBookRepo = userCustomBookRepo;
    this.userMistakeRepo = userMistakeRepo;
  }

  public ResponseEntity<String> getStatistic(Long userId) {
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("mistakeWords", this.countUserMistakeWords(userId));
    jsonObject.put("mistakeSentences", this.countUserMistakeSentence(userId));
    jsonObject.put("learnedWords", this.countUserLearnedWords(userId));
    jsonObject.put("learnedSentences", this.countUserLearnedSentence(userId));
    jsonObject.put("unknownWords", this.countUserUnknownWords(userId));
    jsonObject.put("unknownSentences", this.countUserUnknownSentence(userId));
    jsonObject.put("customWords", this.countUserCustomWords(userId));
    jsonObject.put("customSentences", this.countUserCustomSentence(userId));
    return ResponseEntity.ok(jsonObject.toString());
  }

  private Integer countUserMistakeWords(Long userId) {
    return this.countUserMistake(userId, Constants.UNKNOWN_TYPE_WORD);
  }

  private Integer countUserMistakeSentence(Long userId) {
    return this.countUserMistake(userId, Constants.UNKNOWN_TYPE_SENTENCE);
  }

  private Integer countUserLearnedWords(Long userId) {
    return this.countUserLearned(userId, Constants.UNKNOWN_TYPE_WORD);
  }

  private Integer countUserLearnedSentence(Long userId) {
    return this.countUserLearned(userId, Constants.UNKNOWN_TYPE_SENTENCE);
  }

  private Integer countUserUnknownWords(Long userId) {
    return this.countUserUnknown(userId, Constants.UNKNOWN_TYPE_WORD);
  }

  private Integer countUserUnknownSentence(Long userId) {
    return this.countUserUnknown(userId, Constants.UNKNOWN_TYPE_SENTENCE);
  }

  private Integer countUserCustomWords(Long userId) {
    return this.countUserCustom(userId, Constants.UNKNOWN_TYPE_WORD);
  }

  private Integer countUserCustomSentence(Long userId) {
    return this.countUserCustom(userId, Constants.UNKNOWN_TYPE_SENTENCE);
  }

  private Integer countUserCustom(Long userId, String type) {
    Integer count = 0;
    try {
      if (Constants.UNKNOWN_TYPE_SENTENCE.equals(type)) {
        count = this.userCustomBookRepo.countUserCustomSentencesByUserId(userId);
      } else {
        count = this.userCustomBookRepo.countUserCustomWordsByUserId(userId);
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    return count;
  }

  private Integer countUserUnknown(Long userId, String type) {
    Integer count = 0;
    try {
      if (Constants.UNKNOWN_TYPE_SENTENCE.equals(type)) {
        count = this.userUnknownRepo.countUserUnknownSentencesByUserId(userId);
      } else {
        count = this.userUnknownRepo.countUserUnknownWordsByUserId(userId);
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    return count;
  }

  private Integer countUserMistake(Long userId, String type) {
    Integer count = 0;
    try {
      if (Constants.UNKNOWN_TYPE_SENTENCE.equals(type)) {
        count = this.userMistakeRepo.countUserMistakeWordsByUserId(userId);
      } else {
        count = this.userMistakeRepo.countUserMistakeSentenceByUserId(userId);
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    return count;
  }

  private Integer countUserLearned(Long userId, String type) {
    Integer count = 0;
    try {
      if (Constants.UNKNOWN_TYPE_SENTENCE.equals(type)) {
        count = this.userLearnedSentenceRepo.countLearnedSentencesByUserId(userId);
      } else {
        count = this.userLearnedWordRepo.countLearnedWordsByUserId(userId);
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    return count;
  }


}
