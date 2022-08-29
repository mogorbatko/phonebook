package com.gorbatko.phonebook.controllers;

import com.gorbatko.phonebook.converters.ContactConverter;
import com.gorbatko.phonebook.converters.UserConverter;
import com.gorbatko.phonebook.entities.Contact;
import com.gorbatko.phonebook.entities.User;
import com.gorbatko.phonebook.repositories.ContactRepo;
import com.gorbatko.phonebook.repositories.UserRepo;
import com.gorbatko.phonebook.sources.ContactData;
import com.gorbatko.phonebook.sources.UserData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PhoneBookController {

    @Autowired
    private ContactRepo contactRepo;

    @Autowired
    private UserRepo userRepo;

    @PostMapping("/users")
    public ResponseEntity<User> addUser(@RequestBody UserData userData) {
        try {
            if (userRepo.existsByEmail(userData.getEmail())) {
                return new ResponseEntity<>(null, HttpStatus.NOT_ACCEPTABLE);
            }
            User user = UserConverter.getUserEntityFromData(userData);
            userRepo.save(user);
            return new ResponseEntity<>(user, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/contacts")
    public ResponseEntity<Contact> addContact(@RequestBody ContactData contactData) {
        try {
            Contact contact;
            String userDataEmail = contactData.getUserData().getEmail();

            if (userRepo.existsByEmail(userDataEmail)) {
                User user = userRepo.findByEmail(userDataEmail);
                contact = ContactConverter.getContactEntityFromData(contactData, user);
            } else {
                return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
            }
            contactRepo.save(contact);
            return new ResponseEntity<>(contact, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/users/{email}")
    public ResponseEntity<User> findUserByEmail(@PathVariable String email) {
        if (userRepo.existsByEmail(email)) {
            User user = userRepo.findByEmail(email);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/contacts/{firstName}")
    public ResponseEntity<List<Contact>> findListOfContacts(@PathVariable String firstName) {
        List<Contact> contactList = contactRepo.findByFirstName(firstName);
        return new ResponseEntity<>(contactList, HttpStatus.OK);
    }

    @GetMapping("/contacts/find/{email}")
    public ResponseEntity<List<Contact>> findListOfUserContacts(@PathVariable String email) {
        User user = userRepo.findByEmail(email);
        List<Contact> contactList = contactRepo.findAllByUserId(user.getId());
        return new ResponseEntity<>(contactList, HttpStatus.OK);
    }

    @PutMapping("/contacts/{phoneNumber}")
    public ResponseEntity<Contact> updateContact(@PathVariable String phoneNumber,
                                                 @RequestBody ContactData contactData) {
        try {
            UserData userData = contactData.getUserData();
            String userEmail = userData.getEmail();

            Long userId = userRepo.findByEmail(userEmail).getId();

            Contact contact = contactRepo.findByPhoneNumber(userId, phoneNumber);

            contact.setPhoneNumber(contactData.getPhoneNumber());
            contact.setFirstName(contactData.getFirstName());
            contact.setLastName(contactData.getLastName());

            contactRepo.save(contact);

            return new ResponseEntity<>(contact, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/contacts/{phoneNumber}")
    public ResponseEntity<HttpStatus> deleteContact(@PathVariable String phoneNumber,
                                                    @RequestBody UserData userData) {
        try {
            Long userId = userRepo.findByEmail(userData.getEmail()).getId();

            Contact contact = contactRepo.findByPhoneNumber(userId, phoneNumber);

            contactRepo.delete(contact);

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
