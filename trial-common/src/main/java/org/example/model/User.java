package org.example.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class User {
  Long id;
  String username;
  String firstName;
  String lastName;
}
