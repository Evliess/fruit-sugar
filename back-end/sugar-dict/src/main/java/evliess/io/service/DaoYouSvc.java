package evliess.io.service;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import evliess.io.controller.Constants;
import evliess.io.entity.Sentence;
import evliess.io.entity.UserCustomBook;
import evliess.io.entity.Word;
import evliess.io.jpa.ContentModuleRepo;
import evliess.io.jpa.SentenceRepo;
import evliess.io.jpa.UserCustomBookRepo;
import evliess.io.jpa.WordRepo;
import evliess.io.util.RestUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;

@Slf4j
@Service
public class DaoYouSvc {
  @Value("${audio.dir}")
  private String audioDir;

  @Value("${dyou.app.key}")
  private String APP_KEY;
  @Value("${dyou.app.secret}")
  private String APP_SECRET;
  @Value("${dp.app.secret}")
  private String DP_TOKEN;

  private final WordRepo wordRepo;
  private final SentenceRepo sentenceRepo;
  private final ContentModuleRepo contentModuleRepo;
  private final UserCustomBookRepo userCustomBookRepo;


  @Autowired
  public DaoYouSvc(WordRepo wordRepo, SentenceRepo sentenceRepo
    , ContentModuleRepo contentModuleRepo
    , UserCustomBookRepo userCustomBookRepo) {
    this.wordRepo = wordRepo;
    this.sentenceRepo = sentenceRepo;
    this.contentModuleRepo = contentModuleRepo;
    this.userCustomBookRepo = userCustomBookRepo;
  }

  public ResponseEntity<String> getDigest(String text) {
    JSONObject jsonObject = new JSONObject();
    if (text == null || text.isEmpty()) {
      jsonObject.put(Constants.RESULT, Constants.ERROR);
      return ResponseEntity.ok(jsonObject.toString());
    }
    String digest = RestUtils.getDigest(text);
    jsonObject.put(Constants.RESULT, Constants.OK);
    jsonObject.put("digest", digest);
    return ResponseEntity.ok(jsonObject.toString());
  }

  public ResponseEntity<String> getTextTts(String text) {
    JSONObject jsonObject = new JSONObject();
    if (text == null || text.isEmpty()) {
      jsonObject.put(Constants.RESULT, Constants.ERROR);
      return ResponseEntity.ok(jsonObject.toString());
    }
    String digest = getTextVoice(text);
    this.wordRepo.updateAudioUrlByText(digest, text);
    jsonObject.put(Constants.RESULT, Constants.OK);
    return ResponseEntity.ok(jsonObject.toString());
  }

  private String getTextVoice(String text) {
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
    return digest;
  }

  public ResponseEntity<String> customSentence(Long userId, String sentence) {
    JSONObject jsonObject = new JSONObject();
    if (sentence == null || sentence.isEmpty()) {
      jsonObject.put(Constants.RESULT, Constants.ERROR);
      jsonObject.put(Constants.MSG, "sentence is empty!");
      return ResponseEntity.ok(jsonObject.toString());
    }
    if (this.sentenceRepo.existsByText(sentence)) {
      return handleExistingSentence(userId, sentence, jsonObject);
    }
    return createNewSentence(userId, sentence, jsonObject);
  }

  public ResponseEntity<String> customWord(Long userId, String text) {
    JSONObject jsonObject = new JSONObject();
    if (text == null || text.isEmpty()) {
      jsonObject.put(Constants.RESULT, Constants.ERROR);
      jsonObject.put(Constants.MSG, "sentence is empty!");
      return ResponseEntity.ok(jsonObject.toString());
    }
    if (this.wordRepo.existsByText(text)) {
      return handleExistingWord(userId, text, jsonObject);
    }
    return createNewWord(userId, text, jsonObject);
  }

  private ResponseEntity<String> handleExistingSentence(Long userId, String sentence, JSONObject jsonObject) {
    Sentence sen = this.sentenceRepo.getByText(sentence);
    if (this.userCustomBookRepo.existsByUserIdAndSentenceId(userId, sen.getId())) {
      jsonObject.put(Constants.RESULT, Constants.OK);
      jsonObject.put(Constants.MSG, "Sentence already exists in user's custom book");
      return ResponseEntity.ok(jsonObject.toString());
    }
    addSentenceToUserCustomBook(userId, sen.getId());
    jsonObject.put(Constants.RESULT, Constants.OK);
    return ResponseEntity.ok(jsonObject.toString());
  }

  private ResponseEntity<String> handleExistingWord(Long userId, String text, JSONObject jsonObject) {
    Word word = this.wordRepo.getByText(text);
    if (this.userCustomBookRepo.existsByUserIdAndWordId(userId, word.getId())) {
      jsonObject.put(Constants.RESULT, Constants.OK);
      jsonObject.put(Constants.MSG, "Word already exists in user's custom book");
      return ResponseEntity.ok(jsonObject.toString());
    }
    addWordToUserCustomBook(userId, word.getId());
    jsonObject.put(Constants.RESULT, Constants.OK);
    return ResponseEntity.ok(jsonObject.toString());
  }

  private ResponseEntity<String> createNewSentence(Long userId, String sentence, JSONObject jsonObject) {
    String resp;
    try {
      resp = RestUtils.dpskSentenceChat(sentence, DP_TOKEN);
    } catch (Exception e) {
      jsonObject.put(Constants.RESULT, Constants.ERROR);
      jsonObject.put(Constants.MSG, "LLM error!" + e.getMessage());
      log.error("err: {}", e.getMessage(), e);
      return ResponseEntity.ok(jsonObject.toString());
    }
    JSONObject sentenceData = JSONObject.parse(resp);
    String textTranslation = sentenceData.getString("chinese");
    Sentence sen = createSentenceEntity(sentence, textTranslation);
    this.sentenceRepo.save(sen);
    addSentenceToUserCustomBook(userId, sen.getId());
    jsonObject.put(Constants.RESULT, Constants.OK);
    return ResponseEntity.ok(jsonObject.toString());
  }

  private ResponseEntity<String> createNewWord(Long userId, String text, JSONObject jsonObject) {
    String resp;
    try {
      resp = RestUtils.dpskChat(text, DP_TOKEN);
    } catch (Exception e) {
      jsonObject.put(Constants.RESULT, Constants.ERROR);
      jsonObject.put(Constants.MSG, "LLM error!" + e.getMessage());
      log.error("err: {}", e.getMessage(), e);
      return ResponseEntity.ok(jsonObject.toString());
    }
    Word word = createWordEntity(text, resp);
    this.wordRepo.save(word);
    addWordToUserCustomBook(userId, word.getId());
    jsonObject.put(Constants.RESULT, Constants.OK);
    return ResponseEntity.ok(jsonObject.toString());
  }

  private Word createWordEntity(String text, String resp) {
    JSONObject wordData = JSONObject.parse(resp);
    JSONObject definitionObj = wordData.getJSONObject("part_of_speech");
    String definition = definitionObj.toJSONString();
    JSONObject transcriptionObj = wordData.getJSONObject("transcription");
    String phonetic_us = transcriptionObj.getString("american");
    String phonetic_uk = transcriptionObj.getString("british");
    JSONArray phrasesArray = wordData.getJSONArray("common_phrases");
    JSONArray phrasesJson = new JSONArray();
    for (int i = 0; i < phrasesArray.size(); i++) {
      JSONObject phraseObj = phrasesArray.getJSONObject(i);
      JSONObject phraseJson = new JSONObject();
      phraseJson.put("text", phraseObj.getString("english"));
      phraseJson.put("textTranslation", phraseObj.getString("chinese"));
      phrasesJson.add(phraseJson);
    }
    String audio_us_url = "audio_us_url";
    String audio_uk_url = "audio_uk_url";
    JSONArray sentencesArray = wordData.getJSONArray("example_sentence");
    JSONArray examplesJson = new JSONArray();
    for (int i = 0; i < sentencesArray.size(); i++) {
      JSONObject exampleObj = sentencesArray.getJSONObject(i);
      JSONObject exampleJson = new JSONObject();
      exampleJson.put("text", exampleObj.getString("english"));
      exampleJson.put("textTranslation", exampleObj.getString("chinese"));
      examplesJson.add(exampleJson);
    }
    Word word = new Word();
    word.setText(text);
    word.setDefinition(definition);
    word.setPhoneticUS(phonetic_us);
    word.setPhoneticUK(phonetic_uk);
    word.setPhrases(phrasesJson.toJSONString());
    String digest = this.getTextVoice(text);
    word.setAudioUKUrl(digest + "_uk.mp3");
    word.setAudioUSUrl(digest + "_us.mp3");
    word.setSentences(examplesJson.toJSONString());
    word.setModuleId(contentModuleRepo.getByName(Constants.CUSTOM_WORD_CONTENT_MODULE_NAME).getId());
    return word;
  }


  private Sentence createSentenceEntity(String sentence, String translation) {
    Sentence sen = new Sentence();
    sen.setText(sentence);
    sen.setModuleId(contentModuleRepo.getByName(Constants.CUSTOM_SENTENCE_CONTENT_MODULE_NAME).getId());
    String digest = this.getTextVoice(sentence);
    sen.setAudioUKUrl(digest + "_uk.mp3");
    sen.setAudioUSUrl(digest + "_us.mp3");
    sen.setTextTranslation(translation);
    return sen;
  }

  private void addSentenceToUserCustomBook(Long userId, Long sentenceId) {
    UserCustomBook userCustomBook = new UserCustomBook();
    userCustomBook.setSentenceId(sentenceId);
    userCustomBook.setUserId(userId);
    userCustomBook.setModuleId(this.contentModuleRepo.getByName(Constants.CUSTOM_SENTENCE_CONTENT_MODULE_NAME).getId());
    userCustomBook.setCreatedAt(LocalDateTime.now());
    this.userCustomBookRepo.save(userCustomBook);
  }

  private void addWordToUserCustomBook(Long userId, Long wordId) {
    UserCustomBook userCustomBook = new UserCustomBook();
    userCustomBook.setWordId(wordId);
    userCustomBook.setUserId(userId);
    userCustomBook.setModuleId(this.contentModuleRepo.getByName(Constants.CUSTOM_WORD_CONTENT_MODULE_NAME).getId());
    userCustomBook.setCreatedAt(LocalDateTime.now());
    this.userCustomBookRepo.save(userCustomBook);
  }


}
