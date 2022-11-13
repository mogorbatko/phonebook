package com.gorbatko.phonebook.converters;

import com.gorbatko.phonebook.entities.Contact;
import com.gorbatko.phonebook.entities.User;
import com.gorbatko.phonebook.resources.ContactData;
import com.gorbatko.phonebook.resources.UserData;
import org.springframework.stereotype.Component;

@Component
public class ContactConverter {
    public Contact getContactEntityFromData(ContactData contactData, User user) {
        String phoneNumber = contactData.getPhoneNumber();
        String firstName = contactData.getFirstName();
        String lastName = contactData.getLastName();
        return new Contact(phoneNumber, firstName, lastName, user);
    }

    public Contact getContactEntityFromData(ContactData contactData) {
        String phoneNumber = contactData.getPhoneNumber();
        String firstName = contactData.getFirstName();
        String lastName = contactData.getLastName();
        UserData userData = contactData.getUserData();
        User user = new User(userData.getEmail(), userData.getPassword());
        return new Contact(phoneNumber, firstName, lastName, user);
    }
}
