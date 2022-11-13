package com.gorbatko.phonebook.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "contacts")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @ToString.Exclude
    private Long id;
    private String phoneNumber;
    private String firstName;
    private String lastName;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Contact(String phoneNumber, String firstName, String lastName, User user) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.user = user;
    }
}
