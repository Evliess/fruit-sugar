import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import evliess.io.controller.Constants;
import evliess.io.util.RestUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class RestUtilsTest {

  String APP_KEY = "";
  String APP_SECRET = "";
  String token = "";

  public void test() throws JsonProcessingException {
    String resp = RestUtils.dpskChat("library catalog", token);
    log.info(resp);
  }

  public void getSingleWordSql() throws Exception {
    int module_id = 999;
    String[] words = {"spare tire"};
    for (String word : words) {
      String sql = createSingleWordSql(word, module_id, token);
      System.out.println(sql);
    }
  }

  private String createSingleWordSql(String word, int module_id, String token) throws Exception {
    String resp = RestUtils.dpskChat(word, token);
    JSONObject wordData = JSONObject.parse(resp);
    String text = wordData.getString("word");
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
    StringBuilder sqlBuilder = new StringBuilder("INSERT INTO `words` (`text`, `phonetic_us`, `phonetic_uk`, `definition`, `audio_us_url`, `audio_uk_url`, `phrases`, `example_sentences`, `module_id`) VALUES \n");
    sqlBuilder.append("('")
      .append(text.replace("'", "''"))
      .append("', '")
      .append(phonetic_us != null ? phonetic_us.replace("'", "''") : "")
      .append("', '")
      .append(phonetic_uk != null ? phonetic_uk.replace("'", "''") : "")
      .append("', '")
      .append(definition != null ? definition.replace("'", "''") : "[]")
      .append("', '")
      .append(audio_us_url != null ? audio_us_url.replace("'", "''") : "")
      .append("', '")
      .append(audio_uk_url != null ? audio_uk_url.replace("'", "''") : "")
      .append("', '")
      .append(phrasesJson.toJSONString().replace("'", "''"))
      .append("', '")
      .append(examplesJson.toJSONString().replace("'", "''"))
      .append("', ")
      .append(module_id)
      .append(");");

    return sqlBuilder.toString();
  }


  private void writeToFile(List<String> list, Path filePath) {
    try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {
      for (String line : list) {
        writer.write(line);
        writer.newLine();
      }
      System.out.println("文件写入成功！");
    } catch (IOException e) {
      System.err.println("写入文件失败: " + e.getMessage());
    }
  }

  public void buildWordSqlByModule() throws Exception {
    String dir = "D:\\projects\\learn-dict\\kouyubiaoda\\words\\";
    String targetDir = "D:\\projects\\learn-dict\\kouyubiaoda\\words\\sqls\\";
    String[] filenames = {
      "words_shopping_Clothing & Accessories（服装与配饰）.json"
      , "words_shopping_Cosmetics & Pharmacy（化妆品与药店）.json"
      , "words_shopping_Electronics & Gadgets（电子产品与数码产品）.json"
      , "words_shopping_Online Shopping & Delivery（网购与配送）.json"
      , "words_shopping_Speciality Stores（特色商店）.json"
      , "words_shopping_Stationery & School Supplies（文具与学习用品）.json"
      , "words_shopping_Supermarket & Grocery Shopping（超市与食品采购）.json"
      , "words_Transportation_交通工具租赁.json"
    };
    int moduleId = 111;
    for (String filename : filenames) {
      File jsonFile = new File(dir + filename);
      String content = Files.readString(jsonFile.toPath());
      JSONArray wordArray = JSONArray.parse(content);

      List<String> sqls = new ArrayList<>();
      List<String> errorWords = new ArrayList<>();
      for (int i = 0; i < wordArray.size(); i++) {
        JSONObject sen = wordArray.getJSONObject(i);
        String word = sen.getString("text");
        int random = ThreadLocalRandom.current().nextInt(500, 600);
        Thread.sleep(random);
        String sql;
        try {
          sql = createSingleWordSql(word, moduleId, token);
          sqls.add(sql);
        } catch (Exception e) {
          errorWords.add(word);
          log.error("ERROR: {}", word);
        }

      }
      writeToFile(sqls, Path.of(targetDir, filename.replace(".json", ".md")));
      if (!errorWords.isEmpty()) {
        writeToFile(errorWords, Path.of(targetDir, filename.replace(".json", "_error.md")));
      }
    }
  }

  public void buildWordSqlByModule_2() throws Exception {
    String dir = "D:\\projects\\learn-dict\\kouyubiaoda\\words\\";
    String targetDir = "D:\\projects\\learn-dict\\kouyubiaoda\\words\\sqls\\";
    String[] filenames = {
      "words_Transportation_交通设施（Transportation Facilities）.json"
      , "words_Transportation_公共交通.json"
      , "words_Transportation_打车（Taxi  Ride-hailing）.json"
      , "words_Transportation_机场与火车站.json"
      , "words_Transportation_紧急交通状况.json"
      , "words_Transportation_自行车与步行.json"
      , "words_Transportation_驾车.json"
      , "words_Transportation_规则与安全（Traffic Rules & Safety）.json"
    };
    int moduleId = 222;
    for (String filename : filenames) {
      File jsonFile = new File(dir + filename);
      String content = Files.readString(jsonFile.toPath());
      JSONArray wordArray = JSONArray.parse(content);

      List<String> sqls = new ArrayList<>();
      List<String> errorWords = new ArrayList<>();
      for (int i = 0; i < wordArray.size(); i++) {
        JSONObject sen = wordArray.getJSONObject(i);
        String word = sen.getString("text");
        int random = ThreadLocalRandom.current().nextInt(500, 600);
        Thread.sleep(random);
        String sql;
        try {
          sql = createSingleWordSql(word, moduleId, token);
          sqls.add(sql);
        } catch (Exception e) {
          errorWords.add(word);
          log.error("ERROR: {}", word);
        }

      }
      writeToFile(sqls, Path.of(targetDir, filename.replace(".json", ".md")));
      if (!errorWords.isEmpty()) {
        writeToFile(errorWords, Path.of(targetDir, filename.replace(".json", "_error.md")));
      }
    }
  }

  /**
   * INSERT INTO `words` (`text`, `phonetic_us`, `phonetic_uk`, `definition`, `audio_us_url`, `audio_uk_url`, `phrases`, `example_sentences`, `module_id`) VALUES
   * ('apple', '/ˈæp.əl/', '/ˈæp.əl/', '["a round fruit with firm, white flesh and a green, red, or yellow skin"]', 'https://example.com/audio/apple_us.mp3', 'https://example.com/audio/apple_uk.mp3', '[{"text": "apple pie", "textTranslation": "苹果派"}, {"text": "apple tree", "textTranslation": "苹果树"}]', '[{"text": "I eat an apple every day.", "textTranslation": "我每天吃一个苹果。"}, {"text": "The apple is red and juicy.", "textTranslation": "这个苹果又红又多汁。"}]', 1),
   * ('book', '/bʊk/', '/bʊk/', '["a set of printed pages that are fastened inside a cover", "to arrange to have a seat, room, etc. at a particular time in the future"]', 'https://example.com/audio/book_us.mp3', 'https://example.com/audio/book_uk.mp3', '[{"text": "book club", "textTranslation": "读书俱乐部"}, {"text": "book review", "textTranslation": "书评"}]', '[{"text": "I read a book every week.", "textTranslation": "我每周读一本书。"}, {"text": "Please book a table for two.", "textTranslation": "请预订一张两人桌。"}]', 1),
   * ('computer', '/kəmˈpjuː.t̬ɚ/', '/kəmˈpjuː.tər/', '["an electronic machine that can store and process large amounts of information"]', 'https://example.com/audio/computer_us.mp3', 'https://example.com/audio/computer_uk.mp3', '[{"text": "computer science", "textTranslation": "计算机科学"}, {"text": "computer program", "textTranslation": "计算机程序"}]', '[{"text": "I use my computer for work.", "textTranslation": "我用电脑工作。"}, {"text": "The computer is running slowly today.", "textTranslation": "今天电脑运行很慢。"}]', 2);
   */

  public void getWordSql() throws IOException {
    /**
     * words_Campus_学术研究与写作 (Academic Research & Writing).json
     */
    String dir = "D:\\projects\\learn-dict\\kouyubiaoda\\words\\";
    String fileName = "words_Campus_学术研究与写作 (Academic Research & Writing).json";
    int module_id = 0;
    File jsonFile = new File(dir + fileName);
    String content = Files.readString(jsonFile.toPath());
    JSONArray words = JSONArray.parse(content);
    StringBuilder sqlBuilder = new StringBuilder("INSERT INTO `words` (`text`, `phonetic_us`, `phonetic_uk`, `definition`, `audio_us_url`, `audio_uk_url`, `phrases`, `example_sentences`, `module_id`) VALUES \n");
    for (int i = 0; i < words.size(); i++) {
      JSONObject sen = words.getJSONObject(i);
      String text = sen.getString("text");
      String definition = sen.getString("textTranslation");
      log.info("{}: {}", text, definition);
    }

  }

  public void getSentenceTTSInBatch() throws Exception {
    String[] scenarios = {"sentences_banyinhangka.json", "sentences_canting.json", "sentences_gouwu.json"
      , "sentences_jiantoufa.json", "sentences_jiaotong.json", "sentences_jichang.json"
      , "sentences_jiudian.json", "sentences_kanyisheng.json", "sentences_ketang.json"};
    for (String sce : scenarios) {
      final File filePath = new File("D:\\projects\\learn-dict\\kouyubiaoda\\" + sce);
      String content;
      try {
        content = Files.readString(filePath.toPath());
      } catch (IOException e) {
        log.error("Failed to read: {}", filePath);
        return;
      }
      JSONArray sentences = JSONArray.parse(content);
      for (int i = 0; i < sentences.size(); i++) {
        JSONObject sen = sentences.getJSONObject(i);
        final String text = sen.getString("text");
        final File audioDir = new File("D:\\projects\\learn-dict\\kouyubiaoda\\" + sce.replace(".json", ""));
        if (!audioDir.exists()) audioDir.mkdirs();
        int random = ThreadLocalRandom.current().nextInt(1000, 1500);
        Thread.sleep(random);
        RestUtils.getSentenceTTS(APP_KEY, APP_SECRET, Constants.VOICE_BR, text, audioDir);
        Thread.sleep(random);
        RestUtils.getSentenceTTS(APP_KEY, APP_SECRET, Constants.VOICE_US, text, audioDir);
        Thread.sleep(random);
      }
    }
  }

  public void getSentenceTTS() throws Exception {
    String sentence = "Hi, can I book a table for two at seven tonight?";
    File audioDir = new File("D:\\projects\\learn-dict");
    RestUtils.getSentenceTTS(APP_KEY, APP_SECRET, Constants.VOICE_BR, sentence, audioDir);
  }

  public void insertSentence() {
    Path filePath = Path.of("D:\\projects\\learn-dict\\kouyubiaoda\\sentences_ketang.json");
    int moduleId = 12;
    String content = null;
    try {
      content = Files.readString(filePath);
    } catch (IOException e) {
      return;
    }
    JSONArray sentences = JSONArray.parse(content);
    StringBuilder sqlBuilder = new StringBuilder("INSERT INTO `sentences` (`text`, `text_translation`, `audio_us_url`, `audio_uk_url`, `module_id`) VALUES\n");
    for (int i = 0; i < sentences.size(); i++) {
      JSONObject sen = sentences.getJSONObject(i);
      String text = sen.getString("text");
      String text_translation = sen.getString("textTranslation");
      sqlBuilder.append("  ('")
        .append(text.replace("'", "''"))
        .append("', '")
        .append(text_translation.replace("'", "''"))
        .append("', '")
        .append(RestUtils.getDigest(text)).append("_us.mp3")
        .append("', '")
        .append(RestUtils.getDigest(text)).append("_uk.mp3")
        .append("', ")
        .append(moduleId)
        .append(")");

      if (i < sentences.size() - 1) {
        sqlBuilder.append(",\n");
      } else {
        sqlBuilder.append(";");
      }
    }
    String insertSql = sqlBuilder.toString();
    log.info("\n{}", insertSql);
  }

}
