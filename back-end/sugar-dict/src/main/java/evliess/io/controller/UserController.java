package evliess.io.controller;

import evliess.io.dto.UserDto;
import evliess.io.service.UserSvc;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Users Operations", description = "Users Operations")
public class UserController {
  private final UserSvc userSvc;

  @Autowired
  public UserController(UserSvc userSvc) {
    this.userSvc = userSvc;
  }

  @Operation(summary = "Create or Update a User", description = "Create or Update a User")
  @PostMapping(value = "/private/v1/user/create-or-update", produces = "application/json")
  public ResponseEntity<String> createOrUpdateUser(@RequestBody UserDto userDto) {
    return this.userSvc.createOrUpdateUser(userDto.getName(), userDto.getDays());
  }

  @Operation(summary = "Common User Login", description = "Common User Login")
  @PostMapping(value = "/public/v1/user/login", produces = "application/json")
  public ResponseEntity<String> login(@RequestBody UserDto userDto) {
    return this.userSvc.login(userDto.getCode());
  }

  @Operation(summary = "Admin Login", description = "Admin Login")
  @PostMapping(value = "/public/v1/user/admin-login", produces = "application/json")
  public ResponseEntity<String> adminLogin(@RequestBody UserDto userDto) {
    return this.userSvc.adminLogin(userDto.getName(), userDto.getCode());
  }

  @Operation(summary = "Revoke Token", description = "Revoke Token")
  @PostMapping(value = "/private/v1/user/revoke-token", produces = "application/json")
  public ResponseEntity<String> revoke(@RequestBody UserDto userDto) {
    return this.userSvc.revokeToken(userDto.getCode());
  }


}
