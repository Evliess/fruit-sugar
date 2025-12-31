package evliess.io.service;

import com.alibaba.fastjson2.JSONObject;
import evliess.io.controller.Constants;
import evliess.io.entity.User;
import evliess.io.jpa.UserRepo;
import evliess.io.util.TokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserSvc {
  private final UserRepo userRepo;

  @Autowired
  public UserSvc(UserRepo userRepo) {
    this.userRepo = userRepo;
  }

  public ResponseEntity<String> adminLogin(String name, String code) {
    JSONObject jsonObject = new JSONObject();
    if (name == null || code == null) {
      jsonObject.put(Constants.RESULT, Constants.ERROR);
      jsonObject.put(Constants.MSG, "Login error!");
      return ResponseEntity.ok(jsonObject.toString());
    }
    User user = this.userRepo.getUserByName(name);
    if (user == null) {
      jsonObject.put(Constants.RESULT, Constants.ERROR);
      jsonObject.put(Constants.MSG, "Login error!");
      return ResponseEntity.ok(jsonObject.toString());
    }
    if (code.equals(user.getCode())) {
      jsonObject.put(Constants.RESULT, Constants.OK);
    } else {
      jsonObject.put(Constants.RESULT, Constants.ERROR);
      jsonObject.put(Constants.MSG, "Login error!");
    }
    return ResponseEntity.ok(jsonObject.toString());
  }

  public ResponseEntity<String> login(String token) {
    JSONObject jsonObject = new JSONObject();
    if (token == null || token.isEmpty()) {
      jsonObject.put(Constants.RESULT, Constants.ERROR);
      jsonObject.put(Constants.MSG, "Token error!");
      return ResponseEntity.ok(jsonObject.toString());
    }
    try {
      String[] parts = TokenUtils.decodeToken(token);
      User user = this.userRepo.getUserByName(parts[0]);
      boolean validToken = TokenUtils.isValidToken(parts[1]);
      if (user != null && validToken) {
        jsonObject.put(Constants.RESULT, Constants.OK);
        jsonObject.put("token", parts[1]);
        jsonObject.put("user", parts[0]);
        return ResponseEntity.ok(jsonObject.toString());
      } else {
        jsonObject.put(Constants.RESULT, Constants.ERROR);
        jsonObject.put(Constants.MSG, "Token error!");
      }
    } catch (Exception e) {
      jsonObject.put(Constants.RESULT, Constants.ERROR);
      jsonObject.put(Constants.MSG, "Token error!");
    }
    return ResponseEntity.ok(jsonObject.toString());
  }

  public ResponseEntity<String> createOrUpdateUser(String name, String days) {
    JSONObject jsonObject = new JSONObject();
    if (name == null || name.isEmpty()) {
      jsonObject.put(Constants.RESULT, Constants.ERROR);
      jsonObject.put(Constants.MSG, "User name cannot be null");
      return ResponseEntity.ok(jsonObject.toString());
    }
    User user = this.userRepo.getUserByName(name);
    String token = TokenUtils.generateToken(name, days);
    if (user != null) {
      user.setCode(token);
      jsonObject.put(Constants.MSG, "Update token succeed!");
    } else {
      user = new User();
      user.setName(name);
      user.setCode(token);
      user.setRole(Constants.ROLE_USER);
      jsonObject.put(Constants.MSG, "Create user succeed!");
    }
    this.userRepo.save(user);
    jsonObject.put(Constants.RESULT, Constants.OK);
    jsonObject.put("token", token);
    return ResponseEntity.ok(jsonObject.toString());
  }
}
