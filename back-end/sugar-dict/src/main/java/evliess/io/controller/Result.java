package evliess.io.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> {
  private T data;
  private String message;
  private int code;
  private Boolean success;

  public static <T> Result<T> success(T data, String message) {
    return new Result<>(data, message, 200, true);
  }

  public static <T> Result<T> error(String message, int code) {
    return new Result<>(null, message, 500, false);
  }
}
