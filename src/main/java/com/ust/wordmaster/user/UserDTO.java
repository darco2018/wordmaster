package com.ust.wordmaster.user;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode

public class UserDTO {

    private Long id;
    private String email;
}
