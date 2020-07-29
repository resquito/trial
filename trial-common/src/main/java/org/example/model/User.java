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
  private String username;
  private String firstname;
  private String lastname;
}
