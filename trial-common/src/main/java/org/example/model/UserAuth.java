package org.example.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class UserAuth extends User {
  private String password;
}
