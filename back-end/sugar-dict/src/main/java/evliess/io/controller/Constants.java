package evliess.io.controller;

public class Constants {
  public static final String RESULT = "result";
  public static final String OK = "ok";
  public static final String ERROR = "err";
  public static final String DPSK_CHAT_URL = "https://api.deepseek.com/chat/completions";
  public static final String DPSK_MODEL = "deepseek-chat";
  public static final String WORD_SYS_PROMPT = """
    你是一名中英文语言专家，你只允许回答中英文相关的问题！
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
      "example_sentence": {
        "english": "She has a dream to travel around the world and experience different cultures.",
        "chinese": "她有一个环游世界、体验不同文化的梦想。"
      }
    }
    """;
  public static final String VOICE_BR = "youxiaoguan";
  public static final String VOICE_US = "youxiaodao";
  public static final String YD_TTS_URL = "https://openapi.youdao.com/ttsapi";
}
