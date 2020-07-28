package org.example.model;

import java.util.List;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class UserAuth extends User {
  String password;
  List<UserRoles> roles;
}
