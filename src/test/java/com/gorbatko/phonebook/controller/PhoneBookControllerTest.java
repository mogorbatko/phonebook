package com.gorbatko.phonebook.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gorbatko.phonebook.sources.ContactData;
import com.gorbatko.phonebook.sources.UserData;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PhoneBookControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public static String asJsonString(Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterAll
    void cleanTestDB() {
        jdbcTemplate.execute("DELETE FROM contacts");
        jdbcTemplate.execute("DELETE FROM users");

    }

    @Order(1)
    @Test
    public void addUserTest() throws Exception {

        UserData userData = new UserData("ivan@mail.com", "123");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userData)))
                .andExpect(status().isCreated());

    }

    @Order(2)
    @Test
    public void addUserIfExistTest() throws Exception {

        UserData userData = new UserData("ivan@mail.com", "123");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userData)))
                .andExpect(status().isNotAcceptable());
    }

    @Order(3)
    @Test
    public void addContactIfUserExistsTest() throws Exception {

        UserData userData = new UserData("ivan@mail.com", "123");
        ContactData contactData = new ContactData("911", "Jhon", "Smith", userData);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(contactData)))
                .andExpect(status().isCreated());

    }

    @Order(4)
    @Test
    public void addContactIfUserDoesNotExistTest() throws Exception {
        UserData userData = new UserData("igor@mail.com", "123");
        ContactData contactData = new ContactData("911", "Jhon", "Smith", userData);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(contactData)))
                .andExpect(status().isForbidden());

    }

    @Order(5)
    @Test
    public void findUserByEmailTest() throws Exception {
        String email = "ivan@mail.com";

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/" + email))
                .andExpect(status().isOk());
    }

    @Order(6)
    @Test
    public void findUserByEmailIfUserDoesNotExistTest() throws Exception {
        String email = "igor@mail.com";

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/" + email))
                .andExpect(status().isNotFound());
    }

    @Order(7)
    @Test
    public void findListOfContactsTest() throws Exception {
        String firstName = "Jhon";
        mockMvc.perform(MockMvcRequestBuilders.get("/api/contacts/" + firstName))
                .andExpect(status().isOk());

    }

    @Order(8)
    @Test
    public void findListOfUserContactTest() throws Exception {
        String email = "ivan@mail.com";

        mockMvc.perform(MockMvcRequestBuilders.get("/api/contacts/find/" + email))
                .andExpect(status().isOk());
    }

    @Order(9)
    @Test
    public void findListOfUserContactIfUserDoesNotExistTest() throws Exception {
        String email = "igor@mail.com";

        mockMvc.perform(MockMvcRequestBuilders.get("/api/contacts/find/" + email))
                .andExpect(status().isNotFound());
    }

    @Order(10)
    @Test
    public void updateContactTest() throws Exception {
        String phoneNumber = "911";
        UserData userData = new UserData("ivan@mail.com", "123");
        ContactData contactData = new ContactData("119", "Jhon", "Smith", userData);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/contacts/" + phoneNumber)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(contactData)))
                .andExpect(status().isOk());

    }

    @Order(11)
    @Test
    public void updateContactIfUserDoesNotExistTest() throws Exception {
        String phoneNumber = "911";
        UserData userData = new UserData("igor@mail.com", "123");
        ContactData contactData = new ContactData("119", "Jhon", "Smith", userData);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/contacts/" + phoneNumber)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(contactData)))
                .andExpect(status().isNotFound());

    }

    @Order(12)
    @Test
    public void updateContactIfPhoneNumberDoesNotExistTest() throws Exception {
        String phoneNumber = "901";
        UserData userData = new UserData("ivan@mail.com", "123");
        ContactData contactData = new ContactData("119", "Jhon", "Smith", userData);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/contacts/" + phoneNumber)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(contactData)))
                .andExpect(status().isNotFound());

    }

    @Order(13)
    @Test
    public void deleteContactTest() throws Exception {
        String phoneNumber = "911";
        UserData userData = new UserData("ivan@mail.com", "123");

        mockMvc.perform(MockMvcRequestBuilders.put("/api/contacts/" + phoneNumber)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userData)))
                .andExpect(status().isOk());

    }

}
