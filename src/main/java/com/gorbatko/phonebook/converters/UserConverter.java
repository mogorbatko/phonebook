package com.gorbatko.phonebook.converters;

import com.gorbatko.phonebook.entities.User;
import com.gorbatko.phonebook.sources.UserData;

public class UserConverter {
    public static User getUserEntityFromData(UserData userData) {
        return new User(userData.getEmail(), userData.getPassword(), null);
    }
}
