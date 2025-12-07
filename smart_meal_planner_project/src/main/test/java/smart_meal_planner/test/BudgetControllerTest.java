package smart_meal_planner.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import smart_meal_planner.controller.BudgetController;
import smart_meal_planner.model.Budget;
import smart_meal_planner.model.User;
import smart_meal_planner.service.BudgetService;

class BudgetControllerTest {

    private BudgetController controller;
    private BudgetService budgetService;
    private HttpServletRequest request;
    private HttpSession session;

    @BeforeEach
    void setUp() {
        controller = new BudgetController();
        budgetService = mock(BudgetService.class);
        request = mock(HttpServletRequest.class);
        session = mock(HttpSession.class);

        // Inject the mocked service
        controller.service = budgetService;
    }

    // -------------------------------
    //          GET BUDGET
    // -------------------------------

    @Test
    void testGetBudget_HappyPath_ReturnsBudget() {
        User user = new User();
        user.setUID(10);

        Budget expected = new Budget(10, 150.00);

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(user);
        when(budgetService.getBudgetByUserId(10)).thenReturn(expected);

        Budget result = controller.getBudget(request);

        assertEquals(150.00, result.getAmount());
    }

    @Test
    void testGetBudget_UnhappyPath_NoUserLoggedIn() {
        when(request.getSession(false)).thenReturn(null);

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> controller.getBudget(request)
        );

        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
    }

    // -------------------------------
    //         SET BUDGET
    // -------------------------------

    @Test
    void testSetBudget_HappyPath_NewBudgetCreated() {
        User user = new User();
        user.setUID(20);

        Budget requestBody = new Budget(0, 200.00);

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(user);

        when(budgetService.getBudgetByUserId(20)).thenReturn(null);

        Budget saved = new Budget(20, 200.00);
        when(budgetService.saveBudget(any(Budget.class))).thenReturn(saved);

        Budget result = controller.setBudget(request, requestBody);

        assertEquals(200.00, result.getAmount());
    }

    @Test
    void testSetBudget_HappyPath_UpdateExistingBudget() {
        User user = new User();
        user.setUID(5);

        Budget existing = new Budget(5, 100.00);
        Budget requestBody = new Budget(5, 300.00);

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(user);

        when(budgetService.getBudgetByUserId(5)).thenReturn(existing);
        when(budgetService.saveBudget(existing)).thenReturn(existing);

        Budget result = controller.setBudget(request, requestBody);

        assertEquals(300.00, result.getAmount());
    }

    @Test
    void testSetBudget_UnhappyPath_NoUserLoggedIn() {
        when(request.getSession(false)).thenReturn(null);

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> controller.setBudget(request, new Budget(0, 123))
        );

        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
    }
}
