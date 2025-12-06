package smart_meal_planner.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import smart_meal_planner.controller.ChangePasswordController;
import smart_meal_planner.service.DatabaseCommunicator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChangePasswordControllerTest {

    private ChangePasswordController controller;
    private DatabaseCommunicator userService;

    @BeforeEach
    void setUp() {
        controller = new ChangePasswordController();
        userService = mock(DatabaseCommunicator.class);

        // Inject mock
        controller.userService = userService;
    }

    // -------------------------------
    //      HAPPY PATH TEST
    // -------------------------------

    @Test
    void testChangePassword_HappyPath_ReturnsSuccessMessage() {
        PasswordChangeRequest req = new PasswordChangeRequest();
        req.username = "testUser";
        req.oldPassword = "old123";
        req.newPassword = "new456";

        // Mock service call doing nothing (successful)
        doNothing().when(userService).changePassword("testUser", "old123", "new456");

        String result = controller.changePassword(req);

        assertEquals("Password changed successfully", result);
        verify(userService, times(1)).changePassword("testUser", "old123", "new456");
    }

    // -------------------------------
    //     UNHAPPY PATH TEST
    // -------------------------------

    @Test
    void testChangePassword_UnhappyPath_ServiceThrowsException() {
        PasswordChangeRequest req = new PasswordChangeRequest();
        req.username = "testUser";
        req.oldPassword = "badOldPassword";
        req.newPassword = "new456";

        // Simulate failure in service
        doThrow(new RuntimeException("Incorrect old password"))
                .when(userService)
                .changePassword("testUser", "badOldPassword", "new456");

        String response = controller.changePassword(req);

        assertTrue(response.contains("Error: Incorrect old password"));
        verify(userService, times(1)).changePassword("testUser", "badOldPassword", "new456");
    }
}
