package smart_meal_planner.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import smart_meal_planner.model.Budget;
import smart_meal_planner.model.User;
import smart_meal_planner.service.BudgetService;

@RestController
@RequestMapping("/api/budget")
@CrossOrigin(origins = "http://localhost:3000")
public class BudgetController {

    @Autowired
    private BudgetService service;

    /** Get current user's budget */
    @GetMapping
    public Budget getBudget(HttpServletRequest request) {

        User currentUser = getCurrentUser(request);
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }

        return service.getBudgetByUserId(currentUser.getUID());
    }

    /** Set or change current user's budget */
    @PostMapping
    public Budget setBudget(
            HttpServletRequest request,
            @RequestBody Budget budgetRequest) {

        User currentUser = getCurrentUser(request);
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }

        Budget existing = service.getBudgetByUserId(currentUser.getUID());

        if (existing == null) {
            // Create a new budget record
            Budget newBudget = new Budget(currentUser.getUID(), budgetRequest.getAmount());
            return service.saveBudget(newBudget);
        } else {
            // Update existing budget
            existing.setAmount(budgetRequest.getAmount());
            return service.saveBudget(existing);
        }
    }

    /** Get user from session */
    private User getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return null;

        Object userObj = session.getAttribute("user");
        if (userObj instanceof User) {
            return (User) userObj;
        }

        return null;
    }
}
