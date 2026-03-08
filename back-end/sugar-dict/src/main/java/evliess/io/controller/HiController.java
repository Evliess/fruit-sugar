package evliess.io.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/public/v1")
@Tag(name = "Hi Operations", description = "Hi Operations")
public class HiController {

  @GetMapping("/healthy")
  @Operation(
    summary = "Healthy check",
    description = "Healthy check"
  )
  public Result<List<String>> hi() {
    List<String> list = Arrays.asList("1", "2");
    return Result.success(list, "test");
  }
}
