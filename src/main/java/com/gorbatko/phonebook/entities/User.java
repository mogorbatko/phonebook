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

//    public User() {
//    }

    public User(String email, String password, List<Contact> contactList) {
        this.email = email;
        this.password = password;
        this.contactList = contactList;
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }
//
//    public String getPassword() {
//        return password;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }
//
//    public List<Contact> getContactList() {
//        return contactList;
//    }
//
//    public void setContactList(List<Contact> contactList) {
//        this.contactList = contactList;
//    }
}
