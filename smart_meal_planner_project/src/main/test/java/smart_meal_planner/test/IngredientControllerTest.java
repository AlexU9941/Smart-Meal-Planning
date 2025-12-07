package smart_meal_planner.test;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import smart_meal_planner.controller.IngredientController;
import smart_meal_planner.model.IngredientInput;
import smart_meal_planner.model.User;
import smart_meal_planner.service.IngredientService;

import java.util.Arrays;
import java.util.List;

class IngredientControllerTest {

    private IngredientController controller;
    private IngredientService serviceMock;
    private HttpServletRequest requestMock;
    private HttpSession sessionMock;

    private User testUser;

    @BeforeEach
    void setUp() {
        serviceMock = mock(IngredientService.class);
        controller = new IngredientController();

        requestMock = mock(HttpServletRequest.class);
        sessionMock = mock(HttpSession.class);

        testUser = new User();
        testUser.setUID(1L);
        testUser.setUsername("testuser");

        when(requestMock.getSession(false)).thenReturn(sessionMock);
        when(sessionMock.getAttribute("user")).thenReturn(testUser);
    }

    @Test
    void addIngredients_HappyPath() {
        IngredientInput ing1 = new IngredientInput();
        ing1.setName("Tomato");

        List<IngredientInput> input = Arrays.asList(ing1);

        when(serviceMock.saveIngredients(anyList())).thenReturn(input);

        List<IngredientInput> result = controller.addIngredients(requestMock, input);

        assertEquals(1, result.size());
        assertEquals("Tomato", result.get(0).getName());
        assertEquals(testUser, result.get(0).getUser());
        verify(serviceMock).saveIngredients(input);
    }

    @Test
    void addIngredients_Unhappy_UserNotLoggedIn() {
        when(requestMock.getSession(false)).thenReturn(null);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> controller.addIngredients(requestMock, List.of()));

        assertEquals("User not logged in", ex.getMessage());
    }

    @Test
    void getUserIngredients_HappyPath() {
        IngredientInput ing = new IngredientInput();
        ing.setName("Lettuce");

        when(serviceMock.getIngredientsForUser(testUser.getUID())).thenReturn(List.of(ing));

        List<IngredientInput> result = controller.getUserIngredients(requestMock);

        assertEquals(1, result.size());
        assertEquals("Lettuce", result.get(0).getName());
    }

    @Test
    void getUserIngredients_Unhappy_UserNotLoggedIn() {
        when(sessionMock.getAttribute("user")).thenReturn(null);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> controller.getUserIngredients(requestMock));

        assertEquals("User not logged in", ex.getMessage());
    }

    @Test
    void deleteIngredient_HappyPath() {
        doNothing().when(serviceMock).deleteIngredientForUser(42L, testUser.getUID());

        assertDoesNotThrow(() -> controller.deleteIngredient(requestMock, 42L));

        verify(serviceMock).deleteIngredientForUser(42L, testUser.getUID());
    }

    @Test
    void deleteIngredient_Unhappy_UserNotLoggedIn() {
        when(sessionMock.getAttribute("user")).thenReturn(null);

        assertThrows(org.springframework.web.server.ResponseStatusException.class,
                () -> controller.deleteIngredient(requestMock, 42L));
    }
}
