package evliess.io.controller;

import evliess.io.dto.UserDto;
import evliess.io.service.UserSvc;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public/v1")
@Tag(name = "Users Operations", description = "Users Operations")
public class UserController {
  private final UserSvc userSvc;

  @Autowired
  public UserController(UserSvc userSvc) {
    this.userSvc = userSvc;
  }

  @PostMapping(value = "/user/create-or-update", produces = "application/json")
  public ResponseEntity<String> createOrUpdateUser(@RequestBody UserDto userDto) {
    return this.userSvc.createOrUpdateUser(userDto.getName(), userDto.getDays());
  }

  @PostMapping(value = "/user/login", produces = "application/json")
  public ResponseEntity<String> login(@RequestBody UserDto userDto) {
    return this.userSvc.login(userDto.getCode());
  }


  @PostMapping(value = "/user/admin-login", produces = "application/json")
  public ResponseEntity<String> adminLogin(@RequestBody UserDto userDto) {
    return this.userSvc.adminLogin(userDto.getName(), userDto.getCode());
  }


}
