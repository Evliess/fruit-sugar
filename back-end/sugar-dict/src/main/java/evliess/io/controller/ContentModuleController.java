package evliess.io.controller;

import evliess.io.service.ContentModuleSvc;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public/v1")
@Tag(name = "Content Module Operations", description = "Content Module Operations")
public class ContentModuleController {

  private final ContentModuleSvc contentModuleSvc;

  @Autowired
  public ContentModuleController(ContentModuleSvc contentModuleSvc) {
    this.contentModuleSvc = contentModuleSvc;
  }

  @Operation(
    summary = "Get Children ContentModules By Parent Name",
    description = "Get Children ContentModules By Parent Name"
  )
  @GetMapping(value = "/children-content-module", produces = "application/json")
  public ResponseEntity<String> getChildrenContentModules(
    @Parameter(description = "The name of parent content module", required = true, example = "food")
    @RequestParam String parentName,
    @Parameter(description = "The id of user", required = true, example = "1")
    @RequestParam Long userId
  ) {
    return contentModuleSvc.getChildrenContentModules(parentName, userId);
  }

  @Operation(
    summary = "Get All Children ContentModules",
    description = "Get All Children ContentModules"
  )
  @GetMapping(value = "/all-children-content-module", produces = "application/json")
  public ResponseEntity<String> getAllChildrenContentModules(
  ) {
    return contentModuleSvc.getAllChildrenContentModules();
  }

  @Operation(
    summary = "Get ContentModule By Name",
    description = "Get ContentModule By Name"
  )
  @GetMapping(value = "/content-module/by-name", produces = "application/json")
  public ResponseEntity<String> getByName(
    @Parameter(description = "The name of content module", required = true, example = "hotel")
    @RequestParam String name
  ) {
    return contentModuleSvc.getByName(name);
  }


}
