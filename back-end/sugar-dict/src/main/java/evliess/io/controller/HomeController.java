package evliess.io.controller;

import evliess.io.service.ContentModuleSvc;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public/v1/home")
@Tag(name = "Home Page Operations", description = "Home Page Operations")
public class HomeController {

  private final ContentModuleSvc contentModuleSvc;

  @Autowired
  public HomeController(ContentModuleSvc contentModuleSvc) {
    this.contentModuleSvc=contentModuleSvc;
  }

  @Operation(summary = "Get all content modules", description = "Get all content modules")
  @GetMapping("/all-modules")
  public ResponseEntity<String> getAllContentModules() {
    return contentModuleSvc.getAllContentModules();
  }

}
