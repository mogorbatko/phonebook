package com.gorbatko.phonebook.controllers;

import com.gorbatko.phonebook.converters.ContactConverter;
import com.gorbatko.phonebook.converters.UserConverter;
import com.gorbatko.phonebook.entities.Contact;
import com.gorbatko.phonebook.entities.User;
import com.gorbatko.phonebook.repositories.ContactRepo;
import com.gorbatko.phonebook.repositories.UserRepo;
import com.gorbatko.phonebook.resources.ContactData;
import com.gorbatko.phonebook.resources.ErrorData;
import com.gorbatko.phonebook.resources.UserData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api")
public class PhoneBookController {

    private static Logger logger = Logger.getLogger(PhoneBookController.class.getName());

    @Autowired
    private ContactRepo contactRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ContactConverter contactConverter;

    @Autowired
    private UserConverter userConverter;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorData> handleException(Exception exception) {
        StackTraceElement firstStackTraceElement = Arrays.stream(exception.getStackTrace()).findFirst().get();
        String className = firstStackTraceElement.getClassName();
        String methodName = firstStackTraceElement.getMethodName();
        String message = exception.toString();
        ErrorData error = new ErrorData(className, methodName, message);
        logger.warning(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase() +
                ": " + className + "." + methodName + " - " + message);
        exception.printStackTrace();
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/users")
    public ResponseEntity<User> addUser(@RequestBody UserData userData) {
        if (userRepo.existsByEmail(userData.getEmail())) {
            return new ResponseEntity<>(null, HttpStatus.NOT_ACCEPTABLE);
        }
        User user = userConverter.getUserEntityFromData(userData);
        user = userRepo.save(user);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PostMapping("/contacts")
    public ResponseEntity<Contact> addContact(@RequestParam String userEmail,
                                              @RequestBody ContactData contactData) {
        if (!userRepo.existsByEmail(userEmail)) {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }
        User user = userRepo.findByEmail(userEmail);
        Contact contact = contactConverter.getContactEntityFromData(contactData, user);
        contact = contactRepo.save(contact);
        return new ResponseEntity<>(contact, HttpStatus.CREATED);
    }

    @GetMapping("/users/all")
    public ResponseEntity<List<User>> getAllUsers() {
        return new ResponseEntity<>(userRepo.findAll(), HttpStatus.OK);
    }

    @GetMapping("/contacts/all")
    public ResponseEntity<List<Contact>> getAllContacts() {
        return new ResponseEntity<>(contactRepo.findAll(), HttpStatus.OK);
    }


    @GetMapping("/users")
    public ResponseEntity<User> findUserByEmail(@RequestParam String email) {
        if (!userRepo.existsByEmail(email)) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        User user = userRepo.findByEmail(email);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/contacts/{id}")
    public ResponseEntity<Contact> getContactById(@PathVariable Long id) {
        if (contactRepo.findById(id).isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        Contact contact = contactRepo.findById(id).get();
        return new ResponseEntity<>(contact, HttpStatus.OK);
    }

    @GetMapping("/contacts")
    public ResponseEntity<List<Contact>> findListOfContacts(@RequestParam(required = false) String firstName,
                                                            @RequestParam(required = false) String lastName,
                                                            @RequestParam(required = false) String phoneNumber) {

        List<Contact> contactList = contactRepo.findContacts(firstName, lastName, phoneNumber);
        return new ResponseEntity<>(contactList, HttpStatus.OK);
    }

    @PutMapping("/contacts")
    public ResponseEntity<Contact> updateContact(@RequestParam String userEmail,
                                                 @RequestParam String phoneNumber,
                                                 @RequestBody ContactData contactData) {
        if (!(userRepo.existsByEmail(userEmail) &&
                contactRepo.existsByPhoneNumber(phoneNumber))) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        Contact contact = contactRepo.findByPhoneNumber(userEmail, phoneNumber);
        contact.setPhoneNumber(contactData.getPhoneNumber());
        contact.setFirstName(contactData.getFirstName());
        contact.setLastName(contactData.getLastName());
        contact = contactRepo.save(contact);
        return new ResponseEntity<>(contact, HttpStatus.OK);
    }

    @DeleteMapping("/contacts")
    public ResponseEntity<HttpStatus> deleteContact(@RequestParam String userEmail,
                                                    @RequestParam String phoneNumber) {
        if (!(userRepo.existsByEmail(userEmail) &&
                contactRepo.existsByPhoneNumber(phoneNumber))) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        Contact contact = contactRepo.findByPhoneNumber(userEmail, phoneNumber);
        contactRepo.delete(contact);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
