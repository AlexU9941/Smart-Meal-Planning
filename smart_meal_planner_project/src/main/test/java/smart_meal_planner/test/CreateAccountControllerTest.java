package smart_meal_planner.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import smart_meal_planner.controller.CreateAccountController;
import smart_meal_planner.model.User;
import smart_meal_planner.service.DatabaseCommunicator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CreateAccountControllerTest {

    private CreateAccountController controller;
    private DatabaseCommunicator dbCommunicator;

    @BeforeEach
    void setUp() {
        controller = new CreateAccountController();
        dbCommunicator = mock(DatabaseCommunicator.class);
    }

    // -------------------------------
    //         HAPPY PATH
    // -------------------------------

    @Test
    void testCreateAccount_HappyPath_ReturnsCreatedUser() {
        User requestUser = new User();
        requestUser.setUsername("testUser");
        requestUser.setEmail("test@example.com");

        User savedUser = new User();
        savedUser.setUsername("testUser");
        savedUser.setEmail("test@example.com");

        when(dbCommunicator.saveUser(requestUser)).thenReturn(savedUser);

        ResponseEntity<?> response = controller.createAccount(requestUser);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(savedUser, response.getBody());
        verify(dbCommunicator, times(1)).saveUser(requestUser);
    }

    // -------------------------------
    //    BAD REQUEST (IllegalArgumentException)
    // -------------------------------

    @Test
    void testCreateAccount_InvalidInput_ReturnsBadRequest() {
        User requestUser = new User();
        requestUser.setUsername("");  // invalid example

        when(dbCommunicator.saveUser(requestUser))
                .thenThrow(new IllegalArgumentException("Invalid username"));

        ResponseEntity<?> response = controller.createAccount(requestUser);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid username", response.getBody());
    }

    // -------------------------------
    //   CONFLICT (RuntimeException)
    // -------------------------------

    @Test
    void testCreateAccount_DuplicateUser_ReturnsConflict() {
        User requestUser = new User();
        requestUser.setUsername("existingUser");

        when(dbCommunicator.saveUser(requestUser))
                .thenThrow(new RuntimeException("Username already exists"));

        ResponseEntity<?> response = controller.createAccount(requestUser);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Username already exists", response.getBody());
    }

    // -------------------------------
    //   INTERNAL SERVER ERROR (Generic Exception)
    // -------------------------------

    @Test
    void testCreateAccount_UnexpectedException_ReturnsInternalServerError() {
        User requestUser = new User();
        requestUser.setUsername("testUser");

        when(dbCommunicator.saveUser(requestUser))
                .thenThrow(new RuntimeException("DB exploded"));

        // Wrap the RuntimeException in a generic Exception to hit catch(Exception)
        doThrow(new RuntimeException("Unexpected")).when(dbCommunicator).saveUser(requestUser);

        ResponseEntity<?> response = controller.createAccount(requestUser);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Internal server error", response.getBody());
    }
}
