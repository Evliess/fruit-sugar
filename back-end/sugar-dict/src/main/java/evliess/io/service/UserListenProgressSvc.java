package evliess.io.service;

import com.alibaba.fastjson2.JSONObject;
import evliess.io.controller.Constants;
import evliess.io.dto.UserLearnedDTO;
import evliess.io.entity.UserListenProgress;
import evliess.io.jpa.UserListenProgressRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserListenProgressSvc {
  private final UserListenProgressRepo userListenProgressRepo;

  @Autowired
  public UserListenProgressSvc(UserListenProgressRepo userListenProgressRepo) {
    this.userListenProgressRepo = userListenProgressRepo;
  }

  public ResponseEntity<String> getIndex(UserLearnedDTO userLearnedDTO) {
    JSONObject jsonObject = new JSONObject();
    try {
      UserListenProgress oldProgress = this.userListenProgressRepo.getIndex(userLearnedDTO.getUserId(), userLearnedDTO.getType(), userLearnedDTO.getModuleId());
      if (oldProgress == null) {
        jsonObject.put(Constants.RESULT, Constants.OK);
        jsonObject.put("index", 0);
      } else {
        jsonObject.put("index", oldProgress.getCurrIndex());
      }
    } catch (Exception e) {
      jsonObject.put(Constants.RESULT, Constants.ERROR);
      jsonObject.put(Constants.MSG, e.getMessage());
      log.error(e.getMessage(), e);
    }
    return ResponseEntity.ok(jsonObject.toString());
  }

  public ResponseEntity<String> saveOrUpdate(UserLearnedDTO userLearnedDTO) {
    JSONObject jsonObject = new JSONObject();
    try {
      UserListenProgress oldProgress = this.userListenProgressRepo.getIndex(userLearnedDTO.getUserId(), userLearnedDTO.getType(), userLearnedDTO.getModuleId());
      if (oldProgress == null) {
        UserListenProgress userListenProgress = new UserListenProgress();
        userListenProgress.setUserId(userLearnedDTO.getUserId());
        userListenProgress.setModuleId(userLearnedDTO.getModuleId());
        userListenProgress.setType(userLearnedDTO.getType());
        userListenProgress.setCurrIndex(userLearnedDTO.getCurrIndex());
        this.userListenProgressRepo.save(userListenProgress);
        jsonObject.put(Constants.MSG, "New Progress");
      } else {
        oldProgress.setCurrIndex(userLearnedDTO.getCurrIndex());
        userListenProgressRepo.save(oldProgress);
        jsonObject.put(Constants.MSG, "Update Progress");
      }
      jsonObject.put(Constants.RESULT, Constants.OK);
    } catch (Exception e) {
      jsonObject.put(Constants.RESULT, Constants.ERROR);
      jsonObject.put(Constants.MSG, e.getMessage());
      log.error(e.getMessage(), e);
    }
    return ResponseEntity.ok(jsonObject.toString());
  }
}
