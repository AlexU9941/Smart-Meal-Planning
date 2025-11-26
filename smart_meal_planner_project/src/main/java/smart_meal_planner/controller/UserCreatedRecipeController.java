package smart_meal_planner.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import smart_meal_planner.model.UserCreatedRecipe;
import smart_meal_planner.service.UserCreatedRecipeService;

import java.util.List;

@RestController
@RequestMapping("/user-recipes")
@CrossOrigin(origins = "http://localhost:3000")
public class UserCreatedRecipeController {

    @Autowired
    private UserCreatedRecipeService service;

    @PostMapping
    public UserCreatedRecipe addUserCreatedRecipe(@RequestBody UserCreatedRecipe recipe) {
        return service.saveUserCreatedRecipe(recipe);
    }

    @GetMapping("/{userId}")
    public List<UserCreatedRecipe> getUserCreatedRecipes(@PathVariable Long userId) {
        return service.getUserCreatedRecipesByUserId(userId);
    }
}