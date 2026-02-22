package evliess.io.controller;


import evliess.io.service.UserSummaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/private/v1")
@Tag(name = "User Summary Operations", description = "User Summary Operations")
public class UserSummaryController {

  private final UserSummaryService userSummaryService;

  @Autowired
  public UserSummaryController(UserSummaryService userSummaryService) {
    this.userSummaryService = userSummaryService;
  }

  @Operation(
    summary = "Get Statistic Information User Id",
    description = "Get Statistic Information User Id"
  )
  @GetMapping(value = "/user-statistic/user-id/{userId}", produces = "application/json")
  public ResponseEntity<String> getStatisticUserId(
    @Parameter(description = "userId", required = true, example = "1")
    @PathVariable Long userId
  ) {
    return this.userSummaryService.getStatistic(userId);
  }
}
