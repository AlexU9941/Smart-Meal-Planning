package smart_meal_planner.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import smart_meal_planner.controller.SignInController;
import smart_meal_planner.model.User;
import smart_meal_planner.service.DatabaseCommunicator;
import smart_meal_planner.service.MailService;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

class SignInControllerTest {

    @InjectMocks
    private SignInController controller;

    @Mock
    private DatabaseCommunicator dbCommunicator;

    @Mock
    private MailService mailService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpSession session;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(request.getSession(anyBoolean())).thenReturn(session);
    }

    @Test
    void signIn_HappyPath() {
        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", "testuser");
        credentials.put("password", "pass");

        User mockUser = new User();
        mockUser.setUID(1L);
        mockUser.setUsername("testuser");
        mockUser.setEmail("test@example.com");

        when(dbCommunicator.authenticateUser("testuser", "pass")).thenReturn(mockUser);

        ResponseEntity<?> response = controller.signIn(request, credentials);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("testuser", body.get("username"));
        verify(session, times(1)).setAttribute(eq("user"), eq(mockUser));
    }

    @Test
    void signIn_InvalidCredentials() {
        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", "wrong");
        credentials.put("password", "wrong");

        when(dbCommunicator.authenticateUser("wrong", "wrong"))
                .thenThrow(new RuntimeException("Invalid credentials"));

        ResponseEntity<?> response = controller.signIn(request, credentials);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid credentials", response.getBody());
    }

    @Test
    void logout_HappyPath() {
        ResponseEntity<?> response = controller.logout(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Logged out", response.getBody());
        verify(session, times(1)).invalidate();
    }

    @Test
    void recoverPassword_HappyPath() throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("email", "test@example.com");

        User user = new User();
        user.setEmail("test@example.com");

        when(dbCommunicator.getUserByEmail("test@example.com")).thenReturn(user);

        ResponseEntity<?> response = controller.recoverPassword(body);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Temp password sent.", response.getBody());
        verify(dbCommunicator, times(1)).updateUser(any(User.class));
        verify(mailService, times(1)).sendEmail(eq("test@example.com"), anyString(), anyString());
    }

    @Test
    void recoverPassword_EmailNotFound() {
        Map<String, String> body = new HashMap<>();
        body.put("email", "unknown@example.com");

        when(dbCommunicator.getUserByEmail("unknown@example.com")).thenReturn(null);

        ResponseEntity<?> response = controller.recoverPassword(body);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Email not found", response.getBody());
    }
}
