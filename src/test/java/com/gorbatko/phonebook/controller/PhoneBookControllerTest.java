package com.gorbatko.phonebook.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gorbatko.phonebook.entities.Contact;
import com.gorbatko.phonebook.resources.ContactData;
import com.gorbatko.phonebook.resources.UserData;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Random;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
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

    public String getRandomString() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    public String getRandomPhoneNumber() {
        return "1234567890";
    }

    @AfterEach
    void cleanTestDB() {
        jdbcTemplate.execute("DELETE FROM contacts");
        jdbcTemplate.execute("DELETE FROM users");
    }

    //@Order(1)
    @Test
    public void addUserTest() throws Exception {
        //create user
        String testUserEmail = getRandomString() + "@mail.com";
        String testUserPassword = getRandomString();
        UserData testUserData = new UserData(testUserEmail, testUserPassword);
        //add user to testDB
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testUserData)))
                .andExpect(content().json("{\"email\":\"" + testUserEmail
                        + "\",\"password\":\"" + testUserPassword + "\"}"))
                .andExpect(status().isCreated())
                .andDo(print());
    }

    //    @Order(2)
    @Test
    public void addUserIfExistTest() throws Exception {
        //create user
        String testUserEmail = getRandomString() + "@mail.com";
        String testUserPassword = getRandomString();
        UserData testUserData = new UserData(testUserEmail, testUserPassword);
        //add user to testDB
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testUserData)))
                .andDo(print());
        //add tha same user again
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testUserData)))
                .andExpect(status().isNotAcceptable())
                .andDo(print());
    }

    //    @Order(3)
    @Test
    public void addContactIfUserExistsTest() throws Exception {
        //create user
        String testUserEmail = getRandomString() + "@mail.com";
        String testUserPassword = getRandomString();
        UserData testUserData = new UserData(testUserEmail, testUserPassword);
        //add user to testDB
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testUserData)))
                .andDo(print());
        //create new contact
        String testContactFirstName = getRandomString();
        String testContactLastName = getRandomString();
        String testContactPhoneNumber = getRandomPhoneNumber();
        ContactData testContactData = new ContactData(testContactPhoneNumber, testContactFirstName, testContactLastName);
        //add contact to testDB
        mockMvc.perform(MockMvcRequestBuilders.post("/api/contacts")
                        .param("userEmail", testUserEmail)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testContactData)))
                .andExpect(content().json("{\"phoneNumber\":\"" + testContactPhoneNumber
                        + "\",\"firstName\":\"" + testContactFirstName
                        + "\",\"lastName\":\"" + testContactLastName + "\"}"))
                .andExpect(status().isCreated())
                .andDo(print());
    }

    //@Order(4)
    @Test
    public void addContactIfUserDoesNotExistTest() throws Exception {
        String testUserEmail = getRandomString() + "@mail.com";
        //create contact
        String testContactFirstName = getRandomString();
        String testContactLastName = getRandomString();
        String testContactPhoneNumber = getRandomPhoneNumber();
        ContactData testContactData = new ContactData(testContactPhoneNumber, testContactFirstName, testContactLastName);
        //trying to add contact to testDB
        mockMvc.perform(MockMvcRequestBuilders.post("/api/contacts")
                        .param("userEmail", testUserEmail)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testContactData)))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    //@Order(5)
    @Test
    public void findUserByEmailTest() throws Exception {
        //create user
        String testUserEmail = getRandomString() + "@mail.com";
        String testUserPassword = getRandomString();
        UserData testUserData = new UserData(testUserEmail, testUserPassword);
        //add user to testDB
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testUserData)))
                .andDo(print());
        //trying to get user by email
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users")
                        .param("email", testUserEmail))
                .andExpect(content().json("{\"email\":\"" + testUserEmail
                        + "\",\"password\":\"" + testUserPassword + "\"}"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    //@Order(6)
    @Test
    public void findUserByEmailIfUserDoesNotExistTest() throws Exception {
        //create user
        String testUserEmail = getRandomString() + "@mail.com";
        String testUserPassword = getRandomString();
        UserData testUserData = new UserData(testUserEmail, testUserPassword);
        //add user to testDB
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testUserData)))
                .andDo(print());
        //create users email for request
        String requestTestUserEmail = getRandomString();
        //trying to find user by email
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users")
                        .param("email", requestTestUserEmail))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void getAllUsersTest() throws Exception {
        //create first user
        String testUserEmail1 = getRandomString() + "@mail.com";
        String testUserPassword1 = getRandomString();
        UserData testUserData1 = new UserData(testUserEmail1, testUserPassword1);
        //add first user to testDB
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testUserData1)))
                .andDo(print());
        //create second user
        String testUserEmail2 = getRandomString() + "@mail.com";
        String testUserPassword2 = getRandomString();
        UserData testUserData2 = new UserData(testUserEmail2, testUserPassword2);
        //add second user to testDB
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testUserData2)))
                .andDo(print());
        //trying to get list of users from testDB
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/all"))
                .andExpect(content().json(
                        "[{\"email\":\"" + testUserEmail1 +
                                "\",\"password\":\"" + testUserPassword1 +
                                "\",\"contactList\":[]}," +
                                "{\"email\":\"" + testUserEmail2 +
                                "\",\"password\":\"" + testUserPassword2 +
                                "\",\"contactList\":[]}]"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void getAllContactsTest() throws Exception {
        //creating test User
        String testUserEmail = getRandomString() + "@mail.com";
        String testUserPassword = getRandomString();
        UserData testUserData = new UserData(testUserEmail, testUserPassword);
        //add User to testDB
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testUserData)))
                .andDo(print());
        //create first test Contact
        String testContactFirstName1 = getRandomString();
        String testContactLastName1 = getRandomString();
        String testContactPhoneNum1 = getRandomPhoneNumber();
        ContactData testContactData1 = new ContactData(testContactPhoneNum1,
                testContactFirstName1, testContactLastName1);
        //add first test Contact to test DB and getting response object
        MvcResult resultOfAddingFirstContact = mockMvc
                .perform(MockMvcRequestBuilders
                        .post("/api/contacts")
                        .param("userEmail", testUserEmail)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testContactData1)))
                .andExpect(status().isCreated())
                .andDo(print())
                .andReturn();
        String actualTestContact1AsStr = resultOfAddingFirstContact.getResponse().getContentAsString();
        Contact actualTestContact1 = new ObjectMapper().readValue(actualTestContact1AsStr, Contact.class);
        Long idOfTestContact1 = actualTestContact1.getId();
        //create second test Contact
        String testContactFirstName2 = getRandomString();
        String testContactLastName2 = getRandomString();
        String testContactPhoneNum2 = getRandomPhoneNumber();
        ContactData testContactData2 = new ContactData(testContactPhoneNum2,
                testContactFirstName2, testContactLastName2);
        //add second test Contact to test DB  and getting response object
        MvcResult resultOfAddingSecondContact = mockMvc
                .perform(MockMvcRequestBuilders
                        .post("/api/contacts")
                        .param("userEmail", testUserEmail)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testContactData2)))
                .andExpect(status().isCreated())
                .andDo(print())
                .andReturn();
        String actualTestContact2AsStr = resultOfAddingSecondContact.getResponse().getContentAsString();
        Contact actualTestContact2 = new ObjectMapper().readValue(actualTestContact2AsStr, Contact.class);
        Long idOfTestContact2 = actualTestContact2.getId();

        //trying to get list of contacts from testDB
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/contacts/all"))
                .andExpect(content().json(
                        "[{\"id\":" + idOfTestContact1 +
                                ",\"phoneNumber\":\"" + testContactPhoneNum1 +
                                "\",\"firstName\":\"" + testContactFirstName1 +
                                "\",\"lastName\":\"" + testContactLastName1 +
                                "\"}," +
                                "{\"id\":" + idOfTestContact2 +
                                ",\"phoneNumber\":\"" + testContactPhoneNum2 +
                                "\",\"firstName\":\"" + testContactFirstName2 +
                                "\",\"lastName\":\"" + testContactLastName2 +
                                "\"}]"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void getContactByIdTest() throws Exception {
        //creating test User
        String testUserEmail = getRandomString() + "@mail.com";
        String testUserPassword = getRandomString();
        UserData testUserData = new UserData(testUserEmail, testUserPassword);
        //add User to testDB
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testUserData)))
                .andDo(print());
        //create first test Contact
        String testContactFirstName1 = getRandomString();
        String testContactLastName1 = getRandomString();
        String testContactPhoneNum1 = getRandomPhoneNumber();
        ContactData testContactData1 = new ContactData(testContactPhoneNum1,
                testContactFirstName1, testContactLastName1);
        //add first test Contact to test DB and getting response object
        MvcResult resultOfAddingFirstContact = mockMvc
                .perform(MockMvcRequestBuilders
                        .post("/api/contacts")
                        .param("userEmail", testUserEmail)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testContactData1)))
                .andExpect(status().isCreated())
                .andDo(print())
                .andReturn();
        //getting id of contact entity from response
        String actualTestContact1AsStr = resultOfAddingFirstContact.getResponse().getContentAsString();
        Contact actualTestContact1 = new ObjectMapper().readValue(actualTestContact1AsStr, Contact.class);
        Long idOfTestContact1 = actualTestContact1.getId();
        //create second test Contact
        String testContactFirstName2 = getRandomString();
        String testContactLastName2 = getRandomString();
        String testContactPhoneNum2 = getRandomPhoneNumber();
        ContactData testContactData2 = new ContactData(testContactPhoneNum2,
                testContactFirstName2, testContactLastName2);
        //add second test Contact to test DB  and getting response object
        MvcResult resultOfAddingSecondContact = mockMvc
                .perform(MockMvcRequestBuilders
                        .post("/api/contacts")
                        .param("userEmail", testUserEmail)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testContactData2)))
                .andExpect(status().isCreated())
                .andDo(print())
                .andReturn();
        String actualTestContact2AsStr = resultOfAddingSecondContact.getResponse().getContentAsString();
        Contact actualTestContact2 = new ObjectMapper().readValue(actualTestContact2AsStr, Contact.class);
        Long idOfTestContact2 = actualTestContact2.getId();
        //trying to get contact from testDB by id
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/contacts/{id}", idOfTestContact1))
//                .andExpect(content().json(
//                        "{\"id\":" + idOfTestContact1 +
//                                ",\"phoneNumber\":\"" + testContactPhoneNum1 +
//                                "\",\"firstName\":\"" + testContactFirstName1 +
//                                "\",\"lastName\":\"" + testContactLastName1 +
//                                "\"}"
                .andExpect(status().isOk())
                .andDo(print());
    }

    //@Order(7)
    @Test
    public void findListOfContactsTest() throws Exception {
        //creating test User
        String testUserEmail = getRandomString() + "@mail.com";
        String testUserPassword = getRandomString();
        UserData testUserData = new UserData(testUserEmail, testUserPassword);
        //add User to testDB
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testUserData)))
                .andDo(print());
        //create first test Contact
        String testContactFirstName1 = getRandomString();
        String testContactLastName1 = getRandomString();
        String testContactPhoneNum1 = getRandomPhoneNumber();
        ContactData testContactData1 = new ContactData(testContactPhoneNum1,
                testContactFirstName1, testContactLastName1);
        //add first test Contact to test DB
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/contacts")
                        .param("userEmail", testUserEmail)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testContactData1)))
                .andExpect(status().isCreated())
                .andDo(print());
        //create second test Contact
        String testContactFirstName2 = getRandomString();
        String testContactLastName2 = getRandomString();
        String testContactPhoneNum2 = getRandomPhoneNumber();
        ContactData testContactData2 = new ContactData(testContactPhoneNum2,
                testContactFirstName2, testContactLastName2);
        //add second test Contact to test DB
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/contacts")
                        .param("userEmail", testUserEmail)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testContactData2)))
                .andExpect(status().isCreated())
                .andDo(print());
        //trying to get contact from testDB by phoneNumber
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/contacts")
                        .param("phoneNumber", testContactPhoneNum1))
//                .andExpect(content().json(
//                        "{\"id\":" + idOfTestContact1 +
//                                ",\"phoneNumber\":\"" + testContactPhoneNum1 +
//                                "\",\"firstName\":\"" + testContactFirstName1 +
//                                "\",\"lastName\":\"" + testContactLastName1 +
//                                "\"}"
                .andExpect(status().isOk())
                .andDo(print());
    }

//    @Order(8)
//    @Test
//    public void findListOfUserContactTest() throws Exception {
//        String email = "ivan@mail.com";
//
//        mockMvc.perform(MockMvcRequestBuilders.get("/api/contacts/find/" + email))
//                .andExpect(status().isOk());
//    }

//    @Order(9)
//    @Test
//    public void findListOfUserContactIfUserDoesNotExistTest() throws Exception {
//        String email = "igor@mail.com";
//
//        mockMvc.perform(MockMvcRequestBuilders.get("/api/contacts/find/" + email))
//                .andExpect(status().isNotFound());
//    }

    //@Order(10)
    @Test
    public void updateContactTest() throws Exception {
        //creating test User
        String testUserEmail = getRandomString() + "@mail.com";
        String testUserPassword = getRandomString();
        UserData testUserData = new UserData(testUserEmail, testUserPassword);
        //add User to testDB
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testUserData)))
                .andDo(print());
        //create first test Contact
        String testContactFirstName1 = getRandomString();
        String testContactLastName1 = getRandomString();
        String testContactPhoneNum1 = getRandomPhoneNumber();
        ContactData testContactData1 = new ContactData(testContactPhoneNum1,
                testContactFirstName1, testContactLastName1);
        //add first test Contact to test DB and getting response object
        MvcResult resultOfAddingFirstContact = mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/contacts")
                        .param("userEmail", testUserEmail)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testContactData1)))
                .andExpect(status().isCreated())
                .andDo(print())
                .andReturn();
        String actualTestContact1AsStr = resultOfAddingFirstContact.getResponse().getContentAsString();
        Contact actualTestContact1 = new ObjectMapper().readValue(actualTestContact1AsStr, Contact.class);
        Long idOfTestContact1 = actualTestContact1.getId();
        //create second test Contact
        String testContactFirstName2 = getRandomString();
        String testContactLastName2 = getRandomString();
        String testContactPhoneNum2 = getRandomPhoneNumber();
        ContactData testContactData2 = new ContactData(testContactPhoneNum2,
                testContactFirstName2, testContactLastName2);
        //change first contact to second contact
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/contacts")
                        .param("userEmail", testUserEmail)
                        .param("phoneNumber", testContactPhoneNum1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testContactData2)))
                .andExpect(content().json(
                        "{\"id\":" + idOfTestContact1 +
                                ",\"phoneNumber\":\"" + testContactPhoneNum2 +
                                "\",\"firstName\":\"" + testContactFirstName2 +
                                "\",\"lastName\":\"" + testContactLastName2 +
                                "\"}"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    //@Order(11)
    @Test
    public void updateContactIfUserDoesNotExistTest() throws Exception {
        //creating test User
        String testUserEmail = getRandomString() + "@mail.com";
        String testUserPassword = getRandomString();
        UserData testUserData = new UserData(testUserEmail, testUserPassword);
        //add User to testDB
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testUserData)))
                .andDo(print());
        //create first test Contact
        String testContactFirstName1 = getRandomString();
        String testContactLastName1 = getRandomString();
        String testContactPhoneNum1 = getRandomPhoneNumber();
        ContactData testContactData1 = new ContactData(testContactPhoneNum1,
                testContactFirstName1, testContactLastName1);
        //add first test Contact to test DB
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/contacts")
                        .param("userEmail", testUserEmail)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testContactData1)))
                .andExpect(status().isCreated())
                .andDo(print());
        //create second test Contact
        String testContactFirstName2 = getRandomString();
        String testContactLastName2 = getRandomString();
        String testContactPhoneNum2 = getRandomPhoneNumber();
        ContactData testContactData2 = new ContactData(testContactPhoneNum2,
                testContactFirstName2, testContactLastName2);
        //userEmail for PutMapping
        String testAnotherUserEmail = getRandomString() + "@mail.com";
        //trying to change first contact to second using testAnotherUserEmail
        mockMvc.perform(MockMvcRequestBuilders.put("/api/contacts/")
                        .param("userEmail", testAnotherUserEmail)
                        .param("phoneNumber", testContactPhoneNum1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testContactData2)))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    //@Order(12)
    @Test
    public void updateContactIfPhoneNumberDoesNotExistTest() throws Exception {
        //creating test User
        String testUserEmail = getRandomString() + "@mail.com";
        String testUserPassword = getRandomString();
        UserData testUserData = new UserData(testUserEmail, testUserPassword);
        //add User to testDB
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testUserData)))
                .andDo(print());
        //create first test Contact
        String testContactFirstName1 = getRandomString();
        String testContactLastName1 = getRandomString();
        String testContactPhoneNum1 = getRandomPhoneNumber();
        ContactData testContactData1 = new ContactData(testContactPhoneNum1,
                testContactFirstName1, testContactLastName1);
        //add first test Contact to test DB
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/contacts")
                        .param("userEmail", testUserEmail)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testContactData1)))
                .andExpect(status().isCreated())
                .andDo(print());
        //create second test Contact
        String testContactFirstName2 = getRandomString();
        String testContactLastName2 = getRandomString();
        String testContactPhoneNum2 = getRandomPhoneNumber();
        ContactData testContactData2 = new ContactData(testContactPhoneNum2,
                testContactFirstName2, testContactLastName2);
        //phoneNumber for PutMapping
        String testAnotherPhoneNumber = getRandomPhoneNumber() +"1";
        //trying to change first contact to second using testAnotherPhoneNumber
        mockMvc.perform(MockMvcRequestBuilders.put("/api/contacts/")
                        .param("userEmail", testUserEmail)
                        .param("phoneNumber", testAnotherPhoneNumber)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testContactData2)))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    //@Order(13)
    @Test
    public void deleteContactTest() throws Exception {
        //creating test User
        String testUserEmail = getRandomString() + "@mail.com";
        String testUserPassword = getRandomString();
        UserData testUserData = new UserData(testUserEmail, testUserPassword);
        //add User to testDB
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testUserData)))
                .andDo(print());
        //create first test Contact
        String testContactFirstName1 = getRandomString();
        String testContactLastName1 = getRandomString();
        String testContactPhoneNum1 = getRandomPhoneNumber();
        ContactData testContactData1 = new ContactData(testContactPhoneNum1,
                testContactFirstName1, testContactLastName1);
        //add first test Contact to test DB
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/contacts")
                        .param("userEmail", testUserEmail)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testContactData1)))
                .andExpect(status().isCreated())
                .andDo(print());
        //create second test Contact
        String testContactFirstName2 = getRandomString();
        String testContactLastName2 = getRandomString();
        String testContactPhoneNum2 = getRandomPhoneNumber() + "1";
        ContactData testContactData2 = new ContactData(testContactPhoneNum2,
                testContactFirstName2, testContactLastName2);
        //add second test Contact to test DB
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/contacts")
                        .param("userEmail", testUserEmail)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testContactData2)))
                .andExpect(status().isCreated())
                .andDo(print());
        //delete first contact
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/contacts")
                        .param("userEmail", testUserEmail)
                        .param("phoneNumber", testContactPhoneNum1))
                .andExpect(status().isOk())
                .andDo(print());
    }

}
