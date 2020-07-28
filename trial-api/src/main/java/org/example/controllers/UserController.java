package org.example.controllers;

import java.util.List;
import org.example.model.User;
import org.example.model.UserAuth;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

  @PostMapping(produces = "application/json", consumes = "application/json")
  @PreAuthorize("hasAuthority('ADMIN')")
  public User createUser(@RequestBody UserAuth user) {
    return null;
  }

  @GetMapping(
      params = {"page", "size"},
      produces = "application/json")
  @PreAuthorize("hasAuthority('ADMIN')")
  public List<User> getUsers(@RequestParam("page") int page, @RequestParam("size") int size) {
    return null;
  }

  @GetMapping(value = "/{username}", produces = "application/json")
  @PreAuthorize("hasAuthority('ADMIN') || (authentication.principal == #username)")
  public User getUser(@PathVariable("username") String username) {
    return null;
  }

  @PatchMapping(
      value = "/{username}",
      consumes = "application/json",
      produces = "applicattion/json")
  @PreAuthorize("hasAuthority('ADMIN') || (authentication.principal == #username)")
  public User updateUser(@PathVariable("username") Integer username, @RequestBody UserAuth user) {
    return null;
  }
}
