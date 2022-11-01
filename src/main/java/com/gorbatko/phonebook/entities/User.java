package com.gorbatko.phonebook.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class User {
    @Id
    private String email;
    private String password;
    @OneToMany(mappedBy = "user")
    private List<Contact> contactList;

    public User(String email, String password, List<Contact> contactList) {
        this.email = email;
        this.password = password;
        this.contactList = contactList;
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
