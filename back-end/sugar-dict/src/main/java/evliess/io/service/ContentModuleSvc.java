package evliess.io.service;

import com.alibaba.fastjson2.JSONObject;
import evliess.io.controller.Constants;
import evliess.io.entity.ContentModule;
import evliess.io.jpa.ContentModuleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ContentModuleSvc {
  private final ContentModuleRepo contentModuleRepo;

  @Autowired
  public ContentModuleSvc(ContentModuleRepo contentModuleRepo) {
    this.contentModuleRepo = contentModuleRepo;
  }

  public ResponseEntity<String> getByName(String name) {
    JSONObject jsonObject = new JSONObject();
    if (name == null || name.isBlank()) {
      jsonObject.put(Constants.RESULT, Constants.ERROR);
      return ResponseEntity.ok(jsonObject.toString());
    }
    ContentModule contentModule = this.contentModuleRepo.getByName(name);
    jsonObject.put("name", contentModule.getName());
    jsonObject.put("description", contentModule.getDescription());
    return ResponseEntity.ok(jsonObject.toString());
  }
}
