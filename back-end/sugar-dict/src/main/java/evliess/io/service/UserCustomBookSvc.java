package evliess.io.service;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import evliess.io.controller.Constants;
import evliess.io.entity.Sentence;
import evliess.io.entity.UserCustomBook;
import evliess.io.entity.Word;
import evliess.io.jpa.SentenceRepo;
import evliess.io.jpa.UserCustomBookRepo;
import evliess.io.jpa.WordRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserCustomBookSvc {
  private final UserCustomBookRepo userCustomBookRepo;
  private final WordRepo wordRepo;
  private final SentenceRepo sentenceRepo;

  public UserCustomBookSvc(UserCustomBookRepo userCustomBookRepo
    , WordRepo wordRepo
    , SentenceRepo sentenceRepo) {
    this.userCustomBookRepo = userCustomBookRepo;
    this.wordRepo = wordRepo;
    this.sentenceRepo = sentenceRepo;
  }

  @Transactional
  public ResponseEntity<String> deleteUserCustomWordByUserIdAndWordId(Long userId, Long id, String type) {
    JSONObject jsonObject = new JSONObject();
    if (userId == null || id == null) {
      jsonObject.put(Constants.RESULT, Constants.ERROR);
      jsonObject.put(Constants.MSG, "UserId or id is null");
      log.error("UserId or id is null");
      return ResponseEntity.ok(jsonObject.toString());
    }
    try {
      if (Constants.UNKNOWN_TYPE_WORD.equals(type)) {
        this.userCustomBookRepo.deleteUserCustomWordByUserIdAndWordId(userId, id);
      } else if (Constants.UNKNOWN_TYPE_SENTENCE.equals(type)) {
        this.userCustomBookRepo.deleteUserCustomSentenceByUserIdAndSentenceId(userId, id);
      }
    } catch (Exception e) {
      log.error("Failed to delete user {}, Obj id {}", userId, id, e);
      jsonObject.put(Constants.RESULT, Constants.ERROR);
      jsonObject.put(Constants.MSG, "Failed to delete obj: " + id);
      return ResponseEntity.ok(jsonObject.toString());
    }
    jsonObject.put(Constants.RESULT, Constants.OK);
    return ResponseEntity.ok(jsonObject.toString());
  }

  public ResponseEntity<String> buildSentenceResponseEntity(Long userId) {
    JSONObject jsonObject = new JSONObject();
    List<UserCustomBook> userCustomBooks = this.userCustomBookRepo.getUserCustomSentencesByUserId(userId);
    List<Sentence> sentences = new ArrayList<>();
    for (UserCustomBook userCustomBook : userCustomBooks) {
      Optional<Sentence> optSentence = this.sentenceRepo.findById(userCustomBook.getSentenceId());
      optSentence.ifPresent(sentences::add);
    }
    try {
      List<Long> knownSentences = new ArrayList<>();
      JSONArray wordArr = SentenceSvc.buildSentencesArray(sentences, knownSentences);
      jsonObject.put(Constants.RESULT, Constants.OK);
      jsonObject.put("sentences", wordArr);
      jsonObject.put("count", wordArr.size());
      return ResponseEntity.ok(jsonObject.toString());
    } catch (Exception e) {
      jsonObject.put(Constants.RESULT, Constants.ERROR);
      jsonObject.put("count", 0);
      jsonObject.put("words", new JSONArray());
      log.error("Error retrieving sentences for user ID: {}", userId, e);
      return ResponseEntity.ok(jsonObject.toString());
    }
  }

  public ResponseEntity<String> buildWordResponseEntity(Long userId) {
    JSONObject jsonObject = new JSONObject();
    List<UserCustomBook> userCustomBooks = this.userCustomBookRepo.getUserCustomWordsByUserId(userId);
    List<Word> words = new ArrayList<>();
    for (UserCustomBook userCustomBook : userCustomBooks) {
      Optional<Word> optWord = this.wordRepo.findById(userCustomBook.getWordId());
      optWord.ifPresent(words::add);
    }
    try {
      List<Long> knownWords = new ArrayList<>();
      JSONArray wordArr = WordSvc.buildWordsArray(words, knownWords, false);
      jsonObject.put(Constants.RESULT, Constants.OK);
      jsonObject.put("words", wordArr);
      jsonObject.put("count", wordArr.size());
      return ResponseEntity.ok(jsonObject.toString());
    } catch (Exception e) {
      jsonObject.put(Constants.RESULT, Constants.ERROR);
      jsonObject.put("count", 0);
      jsonObject.put("words", new JSONArray());
      log.error("Error retrieving words for user ID: {}", userId, e);
      return ResponseEntity.ok(jsonObject.toString());
    }
  }
}
