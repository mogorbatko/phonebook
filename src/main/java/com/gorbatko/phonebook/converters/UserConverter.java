package com.gorbatko.phonebook.converters;

import com.gorbatko.phonebook.entities.User;
import com.gorbatko.phonebook.resources.UserData;
import org.springframework.stereotype.Component;

@Component
public class UserConverter {
    public User getUserEntityFromData(UserData userData) {
        return new User(userData.getEmail(), userData.getPassword(), null);
    }
}
