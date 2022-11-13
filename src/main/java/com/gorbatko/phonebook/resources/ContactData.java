package com.gorbatko.phonebook.resources;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ContactData {
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private UserData userData;

    public ContactData(String phoneNumber, String firstName, String lastName) {
        this.phoneNumber = phoneNumber;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
