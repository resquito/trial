package org.example.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.List;
import org.example.model.User;
import org.example.model.UserAuth;
import org.example.model.UserRoles;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

  private UserService userService;

  @Autowired
  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping(produces = "application/json", consumes = "application/json")
  @Operation(security = {@SecurityRequirement(name = "bearer-key")})
  public User createUser(@RequestBody UserAuth user) {
    return userService.createUser(user);
  }

  @GetMapping(
      params = {"page", "size"},
      produces = "application/json")
  @Operation(security = {@SecurityRequirement(name = "bearer-key")})
  public List<User> getUsers(@RequestParam("page") int page, @RequestParam("size") int size) {
    return userService.findAll(page, size);
  }

  @GetMapping(value = "/{username}", produces = "application/json")
  @Operation(security = {@SecurityRequirement(name = "bearer-key")})
  public User getUser(@PathVariable("username") String username) {
    return userService.getUser(username);
  }

  @PatchMapping(value = "/{username}", consumes = "application/json", produces = "application/json")
  @Operation(security = {@SecurityRequirement(name = "bearer-key")})
  public User updateUser(@PathVariable("username") String username, @RequestBody UserAuth user) {
    return userService.updateUser(username, user);
  }

  @PatchMapping(
      value = "/{username}/roles",
      consumes = "application/json",
      produces = "application/json")
  @Operation(security = {@SecurityRequirement(name = "bearer-key")})
  public User updateUser(
      @PathVariable("username") String username, @RequestBody List<UserRoles> roles) {
    return userService.updateRoles(username, roles);
  }
}
