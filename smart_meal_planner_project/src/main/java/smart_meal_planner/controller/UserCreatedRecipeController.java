package smart_meal_planner.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    // @PostMapping
    // public UserCreatedRecipe addUserCreatedRecipe(@RequestBody UserCreatedRecipe recipe) {
    //     System.out.println("Payload received: " + recipe);
    //     System.out.println("UserId type: " + (recipe.getUserId() != null ? recipe.getUserId().getClass().getName() : "null"));
    //     return service.saveUserCreatedRecipe(recipe);
    // }

    @PostMapping
    public ResponseEntity<UserCreatedRecipe> addUserCreatedRecipe(@RequestBody UserCreatedRecipe recipe) {
        UserCreatedRecipe saved = service.saveUserCreatedRecipe(recipe);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{id}")
    public List<UserCreatedRecipe> getUserCreatedRecipes(@PathVariable("id") Long userId) {
        return service.getUserCreatedRecipesByUserId(userId);
    }
 }