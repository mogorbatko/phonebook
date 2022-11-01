package com.gorbatko.phonebook.repositories;

import com.gorbatko.phonebook.entities.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactRepo extends JpaRepository<Contact, Long> {
    boolean existsByPhoneNumber(String phoneNumber);

    @Query("SELECT contact FROM Contact contact WHERE contact.user.id = ?1 AND contact.phoneNumber = ?2")
    Contact findByPhoneNumber(String id, String phoneNumber);

    @Query("SELECT contact FROM Contact contact WHERE contact.firstName = ?1")
    List<Contact> findByFirstName(String firstName);

    @Query("SELECT contact FROM Contact contact WHERE (:firstName IS NULL OR contact.firstName = :firstName) " +
            "AND (:lastName IS NULL OR contact.lastName = :lastName) " +
            "AND (:phoneNumber IS NULL OR contact.phoneNumber = :phoneNumber)")
    List<Contact> findContacts(@Param("firstName") String firstName,
                               @Param("lastName") String lastName,
                               @Param("phoneNumber") String phoneNumber);
}
