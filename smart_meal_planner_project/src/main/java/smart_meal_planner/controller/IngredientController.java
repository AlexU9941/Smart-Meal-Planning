package smart_meal_planner.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import smart_meal_planner.model.IngredientInput;
import smart_meal_planner.model.User;
import smart_meal_planner.service.IngredientService;

@RestController
@RequestMapping("/api/ingredients")
@CrossOrigin(origins = "http://localhost:3000")
public class IngredientController {

    @Autowired
    private IngredientService service;

    @PostMapping
    public List<IngredientInput> addIngredients(
            HttpServletRequest request,
            @RequestBody List<IngredientInput> ingredients) {

        User currentUser = getCurrentUser(request);
        if (currentUser == null) {
            throw new RuntimeException("User not logged in");
        }

        for (IngredientInput ing : ingredients) {
            ing.setUser(currentUser);
        }

        return service.saveIngredients(ingredients);
    }

    @GetMapping
    public List<IngredientInput> getUserIngredients(HttpServletRequest request) {
        User currentUser = getCurrentUser(request);
        if (currentUser == null) {
            throw new RuntimeException("User not logged in");
        }

        return service.getIngredientsForUser(currentUser.getUID());
    }

    @DeleteMapping("/{id}")
    public void deleteIngredient(
            HttpServletRequest request,
            @PathVariable("id") Long id) {

        User currentUser = getCurrentUser(request);
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }

        service.deleteIngredientForUser(id, currentUser.getUID());
    }


    private User getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }

        Object userObj = session.getAttribute("user");
        if (userObj instanceof User) {
            return (User) userObj;
        }

        return null;
    }
}
