package evliess.io.service;


import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import evliess.io.controller.Constants;
import evliess.io.entity.Sentence;
import evliess.io.jpa.SentenceRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class SentenceSvc {
  private final SentenceRepo sentenceRepo;

  @Autowired
  public SentenceSvc(SentenceRepo sentenceRepo) {
    this.sentenceRepo = sentenceRepo;
  }

  public ResponseEntity<String> getSentencesByModuleId(Long moduleId) {
    JSONObject jsonObject = new JSONObject();
    if (moduleId == null) {
      jsonObject.put(Constants.RESULT, Constants.ERROR);
      jsonObject.put(Constants.MSG, "Module ID cannot be null");
      log.error("module id is null.");
      return ResponseEntity.ok(jsonObject.toString());
    }

    try {
      List<Sentence> sentences = this.sentenceRepo.getSentencesByModuleId(moduleId);
      if (sentences == null || sentences.isEmpty()) {
        jsonObject.put(Constants.RESULT, Constants.ERROR);
        jsonObject.put(Constants.MSG, "No sentences found for module ID: " + moduleId);
        jsonObject.put("sentences", new JSONArray());
        jsonObject.put("count", 0);
        return ResponseEntity.ok(jsonObject.toString());
      }
      JSONArray sentenceArr = buildSentencesArray(sentences);
      jsonObject.put(Constants.RESULT, Constants.OK);
      jsonObject.put("sentences", sentenceArr);
      jsonObject.put("count", sentenceArr.size());
      log.info("Retrieved {} sentences for module ID: {}", sentenceArr.size(), moduleId);
      return ResponseEntity.ok(jsonObject.toString());
    } catch (Exception e) {
      jsonObject.put(Constants.RESULT, Constants.ERROR);
      jsonObject.put(Constants.MSG, "Error retrieving sentences: " + e.getMessage());
      log.error("Error retrieving sentences for module ID: {}", moduleId, e);
      return ResponseEntity.ok(jsonObject.toString());
    }
  }

  private static JSONArray buildSentencesArray(List<Sentence> sentences) {
    JSONArray sentenceArr = new JSONArray();
    for (Sentence sentence : sentences) {
      JSONObject sentenceObj = new JSONObject();
      sentenceObj.put("id", sentence.getId());
      sentenceObj.put("text", sentence.getText());
      sentenceObj.put("textTranslation", sentence.getTextTranslation());
      sentenceObj.put("audioUSUrl", sentence.getAudioUSUrl());
      sentenceObj.put("audioUKUrl", sentence.getAudioUKUrl());
      sentenceObj.put("moduleId", sentence.getModuleId());
      sentenceArr.add(sentenceObj);
    }
    return sentenceArr;
  }
}
