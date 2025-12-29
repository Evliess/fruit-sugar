package evliess.io.service;

import com.alibaba.fastjson2.JSONObject;
import evliess.io.controller.Constants;
import evliess.io.jpa.WordRepo;
import evliess.io.util.RestUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;

@Slf4j
@Service
public class DaoYouSvc {
  @Value("${audio.dir}")
  private String audioDir;

  @Value("${dyou.app.key}")
  private String APP_KEY;
  @Value("${dyou.app.secret}")
  private String APP_SECRET;

  private final WordRepo wordRepo;


  @Autowired
  public DaoYouSvc(WordRepo wordRepo) {
    this.wordRepo = wordRepo;
  }

  public ResponseEntity<String> getTextTts(String text) {
    JSONObject jsonObject = new JSONObject();
    if (text == null || text.isEmpty()) {
      jsonObject.put(Constants.RESULT, Constants.ERROR);
      return ResponseEntity.ok(jsonObject.toString());
    }
    String digest = RestUtils.getDigest(text);
    File audioUS = new File(audioDir + "custom" + File.separator + digest + "_us.mp3");
    File audioUK = new File(audioDir + "custom" + File.separator + digest + "_uk.mp3");
    if (!audioUS.exists() && !audioUK.exists()) {
      try {
        RestUtils.getSentenceTTS(APP_KEY, APP_SECRET, Constants.VOICE_US, text, new File(audioDir + "custom" + File.separator));
      } catch (Exception e) {
        log.error("Failed to get tts for: {} us voice", text, e);
      }
      try {
        Thread.sleep(200);
        RestUtils.getSentenceTTS(APP_KEY, APP_SECRET, Constants.VOICE_BR, text, new File(audioDir + "custom" + File.separator));
      } catch (Exception e) {
        log.error("Failed to get tts for: {} uk voice", text, e);
      }
    }
    this.wordRepo.updateAudioUrlByText(digest, text);
    jsonObject.put(Constants.RESULT, Constants.OK);
    return ResponseEntity.ok(jsonObject.toString());
  }
}
