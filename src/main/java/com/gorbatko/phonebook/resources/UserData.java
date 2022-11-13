package com.gorbatko.phonebook.resources;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class UserData {
    private String email;
    private String password;
}
