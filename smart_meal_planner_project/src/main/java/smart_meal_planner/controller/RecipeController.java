package smart_meal_planner.controller;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import smart_meal_planner.service.RecipeSearchRequest;
import smart_meal_planner.service.RecipeService;

@RestController
public class RecipeController {
    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    //Will need to update to match RecipeService method signature
    //or create multiple endpoints for different search criteria
    @PostMapping("/recipes")
    public String getRecipes(@RequestBody RecipeSearchRequest request) {
        return recipeService.findRecipeByIngredients(request.getIngredients(), request.getMaxPrice());
    }

}
