package evliess.io.controller;

public class Constants {
  public static final String RESULT = "result";
  public static final String COUNT = "count";
  public static final String MSG = "msg";
  public static final String OK = "ok";
  public static final String ERROR = "err";
  public static final String DPSK_CHAT_URL = "https://api.deepseek.com/chat/completions";
  public static final String DPSK_MODEL = "deepseek-chat";
  public static final String SENTENCE_SYS_PROMPT = """
    你是一名中英文语言专家，根据我提供的句子，按照下面JSON格式返回！
    Example Input:
    Hi, can I book a table for two at seven tonight?

    EXAMPLE JSON OUTPUT:
    {"chinese": "你好，我想预定今晚七点的两人桌。"}

    """;
  public static final String WORD_SYS_PROMPT = """
    你是一名中英文语言专家，根据我提供的单词或者短语，按照下面JSON格式返回！
    Example Input:
    world

    EXAMPLE JSON OUTPUT:
    {
      "word": "world",
      "part_of_speech": {
        "noun": "世界；地球；领域；人世；大量；人类社会",
        "adjective": "世界范围的；全世界的"
      },
      "transcription": {
        "british": "/wɜːld/",
        "american": "/wɜːrld/"
      },
      "common_phrases": [
        {
          "english": "around the world",
          "chinese": "世界各地"
        },
        {
          "english": "Third World",
          "chinese": "第三世界"
        }
      ],
      "example_sentence": [{
        "english": "She has a dream to travel around the world and experience different cultures.",
        "chinese": "她有一个环游世界、体验不同文化的梦想。"
      }]
    }
    """;
  public static final String SMART_SYS_PROMPT = """
    # Role
    你是一位精通雅思、托福、商务及日常英语的语言专家，具备极高的安全边界意识。

    # Core Logic & Constraints
    - **输入识别**：识别用户的输入 $input 是单个词汇、短语，还是翻译/场景请求。
    - **领域锁定**：仅处理语言学习相关内容。若 $input 涉及政治、代码、医疗建议等非语言学习领域，必须礼貌拒绝。
    - **风格标准**：遵循 IBM/Apple 的极简信息架构，使用 Markdown 格式，保持高扫描性。

    # Task Protocols

    ## CASE A: 输入为单个单词或短语 (Vocabulary Mode)
    按以下结构返回：
    ## 🔤 Expression: [单词/短语]
    **词性：** [POS]
    ---
    ### 🔗 常用词组 (Collocations)
    1. **[英文短语]** — [中文释义]
    2. **[英文短语]** — [中文释义]
    ---
    ### 📝 精选例句 (Exemplary Sentences)
    - [英文例句]
    - [中文翻译]

    ## CASE B: 输入为句子、场景或翻译请求 (Scenario Mode)
    按以下结构返回：
    ## 💬 Scenario: [场景描述/翻译请求]
    **推荐表达：** [核心译文/常用句式]
    ---
    ### 💡 进阶表达 (Alternative Ways)
    - **正式/商务：** [更正式的表达]
    - **地道口语：** [更口语化的表达]
    ---
    ### 🛠 语言点解析 (Key Notes)
    - [解析语法、词汇用法或文化背景]
    """;
  public static final String VOICE_BR = "youxiaoguan"; //UK
  public static final String VOICE_US = "youxiaodao";  //US
  public static final String YD_TTS_URL = "https://openapi.youdao.com/ttsapi";

  public static final String[] SENTENCE_CASES = {"restaurant", "airport", "hotel", "bank_card"
    , "class_discussion", "shop", "hair_cut", "hospital", "transport"};
  public static final String[] WORD_CASES = {"school", "food", "housing", "shopping"
    , "leisure", "city", "health"};
  public static final String[] WORD_HEALTH_CASES = {"body_anatomy", "illnesses_symptoms", "medical_facilities", "healthcare"
    , "treatments", "medications", "first_aid", "insurance"};
  public static final String[] WORD_CITY_CASES = {"transportation_facilities", "traffic_rules", "taxi", "driving"
    , "public_transportation", "cycling", "airports_train_station", "vehicle_rental", "traffic_incidents"};
  public static final String[] WORD_LEISURE_CASES = {"indoor_games", "outdoor_sports", "travel", "music"
    , "nightlife", "fitness", "leisure_services"};
  public static final String[] WORD_SHOPPING_CASES = {"cosmetics", "clothing", "supermarket", "online_shopping"
    , "electronics", "stationery", "speciality_stores"};
  public static final String[] WORD_HOUSING_CASES = {"housing_type", "kitchen", "bathroom", "living_room"
    , "bedroom", "storage_space", "balcony", "interior_design", "maintenance"};
  public static final String[] WORD_FOOD_CASES = {"grains_staples", "legumes_pulses", "dairy", "snacks"
    , "herbs", "oils", "beverages"};
  public static final String[] WORD_SCHOOL_CASES = {"course_study", "exams_assessment", "classroom_activity", "campus_facilities"
    , "academic_research", "study_affairs", "campus_life"};

  public static final String ROLE_ADMIN = "admin";
  public static final String ROLE_USER = "user";
  public static final String MODULE_CUSTOM_WORDS = "custom_words";
  public static final String MODULE_CUSTOM_SENTENCES = "custom_sentences";
  public static final String UNKNOWN_TYPE_WORD = "word";
  public static final String UNKNOWN_TYPE_SENTENCE = "sentence";
  public static final String CUSTOM_WORD_CONTENT_MODULE_NAME = "custom_words";
  public static final String CUSTOM_SENTENCE_CONTENT_MODULE_NAME = "custom_sentences";
  public static final String HEAD_AUTH = "Authorization";
  public static final String BEARER = "Bearer ";
  public static final String SERVLET_CONTEXT_PATH = "/sugar-dict";
}
