package evliess.io.controller;

public class Constants {
  public static final String RESULT = "result";
  public static final String MSG = "msg";
  public static final String OK = "ok";
  public static final String ERROR = "err";
  public static final String DPSK_CHAT_URL = "https://api.deepseek.com/chat/completions";
  public static final String DPSK_MODEL = "deepseek-chat";
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

}
