package evliess.io.service;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import evliess.io.controller.Constants;
import evliess.io.entity.ContentModule;
import evliess.io.entity.UserLearnedWord;
import evliess.io.jpa.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ContentModuleSvc {
  private final ContentModuleRepo contentModuleRepo;
  private final WordRepo wordRepo;
  private final SentenceRepo sentenceRepo;
  private final UserLearnedWordRepo userLearnedWordRepo;
  private final UserWordProgressRepo userWordProgressRepo;

  @Autowired
  public ContentModuleSvc(ContentModuleRepo contentModuleRepo
    , WordRepo wordRepo
    , SentenceRepo sentenceRepo
    , UserLearnedWordRepo userLearnedWordRepo
    , UserWordProgressRepo userWordProgressRepo) {
    this.contentModuleRepo = contentModuleRepo;
    this.wordRepo = wordRepo;
    this.sentenceRepo = sentenceRepo;
    this.userLearnedWordRepo = userLearnedWordRepo;
    this.userWordProgressRepo = userWordProgressRepo;
  }

  public ResponseEntity<String> getAllChildrenContentModules() {
    JSONObject jsonObject = new JSONObject();
    List<ContentModule> childCases = this.contentModuleRepo.getByNames(Constants.WORD_SCHOOL_CASES);
    childCases.addAll(this.contentModuleRepo.getByNames(Constants.WORD_FOOD_CASES));
    childCases.addAll(this.contentModuleRepo.getByNames(Constants.WORD_SHOPPING_CASES));
    childCases.addAll(this.contentModuleRepo.getByNames(Constants.WORD_HOUSING_CASES));
    childCases.addAll(this.contentModuleRepo.getByNames(Constants.WORD_LEISURE_CASES));
    childCases.addAll(this.contentModuleRepo.getByNames(Constants.WORD_CITY_CASES));
    childCases.addAll(this.contentModuleRepo.getByNames(Constants.WORD_HEALTH_CASES));
    JSONArray children = new JSONArray();
    jsonObject.put("children", children);
    for (ContentModule cm : childCases) {
      JSONObject cmObj = new JSONObject();
      cmObj.put("id", cm.getId());
      cmObj.put("name", cm.getName());
      cmObj.put("description", cm.getDescription());
      cmObj.put("wordsCount", this.wordRepo.countWordsByModuleId(cm.getId()));
      children.add(cmObj);
    }
    return ResponseEntity.ok(jsonObject.toString());
  }

  public ResponseEntity<String> getChildrenContentModules(String parentName, Long userId) {
    JSONObject jsonObject = new JSONObject();
    if (parentName == null || parentName.isEmpty()) {
      jsonObject.put(Constants.RESULT, Constants.ERROR);
      return ResponseEntity.ok(jsonObject.toString());
    }
    List<ContentModule> childCases = switch (parentName) {
      case "school" -> this.contentModuleRepo.getByNames(Constants.WORD_SCHOOL_CASES);
      case "food" -> this.contentModuleRepo.getByNames(Constants.WORD_FOOD_CASES);
      case "shopping" -> this.contentModuleRepo.getByNames(Constants.WORD_SHOPPING_CASES);
      case "housing" -> this.contentModuleRepo.getByNames(Constants.WORD_HOUSING_CASES);
      case "leisure" -> this.contentModuleRepo.getByNames(Constants.WORD_LEISURE_CASES);
      case "city" -> this.contentModuleRepo.getByNames(Constants.WORD_CITY_CASES);
      case "health" -> this.contentModuleRepo.getByNames(Constants.WORD_HEALTH_CASES);
      default -> null;
    };
    if (childCases == null) {
      jsonObject.put(Constants.RESULT, Constants.ERROR);
      return ResponseEntity.ok(jsonObject.toString());
    }
    JSONArray children = new JSONArray();
    jsonObject.put("children", children);
    for (ContentModule cm : childCases) {
      JSONObject cmObj = new JSONObject();
      cmObj.put("id", cm.getId());
      cmObj.put("name", cm.getName());
      cmObj.put("description", cm.getDescription());
      cmObj.put("wordsCount", this.wordRepo.countWordsByModuleId(cm.getId()));
      List<UserLearnedWord> knownWords = this.userLearnedWordRepo.findLearnedWordIds(userId, cm.getId());
      cmObj.put("learnedCount", (knownWords == null || knownWords.isEmpty()) ? 0 : knownWords.size());
      children.add(cmObj);
    }
    return ResponseEntity.ok(jsonObject.toString());
  }

  public ResponseEntity<String> getAllContentModules() {
    JSONObject jsonObject = new JSONObject();
    List<ContentModule> sentenceCases = this.contentModuleRepo.getByNames(Constants.SENTENCE_CASES);
    JSONArray sentences = new JSONArray();
    jsonObject.put("sentenceCases", sentences);
    for (ContentModule cm : sentenceCases) {
      JSONObject sentence = new JSONObject();
      sentence.put("id", cm.getId());
      sentence.put("name", cm.getName());
      sentence.put("description", cm.getDescription());
      sentence.put("sentencesCount", this.sentenceRepo.countSentencesByModuleId(cm.getId()));
      sentences.add(sentence);
    }
    JSONArray words = new JSONArray();
    jsonObject.put("wordCases", words);
    List<ContentModule> wordCases = this.contentModuleRepo.getByNames(Constants.WORD_CASES);
    for (ContentModule cm : wordCases) {
      JSONObject word = new JSONObject();
      word.put("id", cm.getId());
      word.put("name", cm.getName());
      word.put("description", cm.getDescription());
      words.add(word);
    }
    return ResponseEntity.ok(jsonObject.toString());
  }

  public ResponseEntity<String> getByName(String name) {
    JSONObject jsonObject = new JSONObject();
    if (name == null || name.isBlank()) {
      jsonObject.put(Constants.RESULT, Constants.ERROR);
      return ResponseEntity.ok(jsonObject.toString());
    }
    ContentModule contentModule = this.contentModuleRepo.getByName(name);
    jsonObject.put(Constants.RESULT, Constants.OK);
    jsonObject.put("name", contentModule.getName());
    jsonObject.put("description", contentModule.getDescription());
    return ResponseEntity.ok(jsonObject.toString());
  }

  @Transactional
  public ResponseEntity<String> resetLearnedCount(Long userId, Long moduleId) {
    JSONObject jsonObject = new JSONObject();
    if (userId == null || moduleId == null) {
      jsonObject.put(Constants.RESULT, Constants.ERROR);
      return ResponseEntity.ok(jsonObject.toString());
    }
    this.userLearnedWordRepo.deleteByUserIdAndModuleId(userId, moduleId);
    this.userWordProgressRepo.resetLearnedCount(userId, moduleId);
    jsonObject.put(Constants.RESULT, Constants.OK);
    return ResponseEntity.ok(jsonObject.toString());
  }
}
