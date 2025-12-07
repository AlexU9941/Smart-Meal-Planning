package smart_meal_planner.test;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import smart_meal_planner.controller.LogoutController;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

class LogoutControllerTest {

    private LogoutController controller;
    private HttpServletRequest requestMock;
    private HttpSession sessionMock;

    @BeforeEach
    void setUp() {
        controller = new LogoutController();
        requestMock = mock(HttpServletRequest.class);
        sessionMock = mock(HttpSession.class);

        when(requestMock.getSession()).thenReturn(sessionMock);
    }

    @Test
    void logout_HappyPath() {
        ResponseEntity<String> response = controller.logout(requestMock);

        // Verify session invalidated
        verify(sessionMock).invalidate();

        // Check response
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("User logged out successfully.", response.getBody());
    }

    @Test
    void logout_NullSession() {
        // Simulate getSession() returning null (rare, but defensive)
        when(requestMock.getSession()).thenReturn(null);

        assertThrows(NullPointerException.class, () -> controller.logout(requestMock));
    }
}
