package com.gorbatko.phonebook.repositories;

import com.gorbatko.phonebook.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User, String> {
    User findByEmail(String email);

    boolean existsByEmail(String email);
}

