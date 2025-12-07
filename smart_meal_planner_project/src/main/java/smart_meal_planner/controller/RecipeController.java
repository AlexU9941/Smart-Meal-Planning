package smart_meal_planner.controller;

import org.springframework.web.bind.annotation.*;

import smart_meal_planner.model.RecipeEntity;
import smart_meal_planner.service.RecipeService;

@RestController
@RequestMapping("/recipes")
public class RecipeController {

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    // Gets an alternative recipe
    @GetMapping("/{id}/alternative")
    public RecipeEntity getAlternative(@PathVariable Long id) {
        return recipeService.getAlternativeMeal(id);
    }
}
