package com.gorbatko.phonebook.converters;

import com.gorbatko.phonebook.entities.Contact;
import com.gorbatko.phonebook.entities.User;
import com.gorbatko.phonebook.sources.ContactData;
import com.gorbatko.phonebook.sources.UserData;

public class ContactConverter {

    public static Contact getContactEntityFromData(ContactData contactData, User user) {

        String phoneNumber = contactData.getPhoneNumber();
        String firstName = contactData.getFirstName();
        String lastName = contactData.getLastName();

        Contact contact = new Contact(phoneNumber, firstName, lastName, user);

        return contact;
    }

    public static Contact getContactEntityFromData(ContactData contactData) {

        String phoneNumber = contactData.getPhoneNumber();
        String firstName = contactData.getFirstName();
        String lastName = contactData.getLastName();
        UserData userData = contactData.getUserData();

        User user = new User(userData.getEmail(), userData.getPassword());
        Contact contact = new Contact(phoneNumber, firstName, lastName, user);

        return contact;
    }

}
