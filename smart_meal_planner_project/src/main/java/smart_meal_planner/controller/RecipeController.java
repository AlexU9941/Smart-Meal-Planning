package smart_meal_planner.controller;

import smart_meal_planner.model.RecipeEntity;
import smart_meal_planner.recipe.RecipeResult;
import smart_meal_planner.model.MealPlan;
import smart_meal_planner.dto.Ingredient;
import smart_meal_planner.dto.RecipeRecommendationDTO;
import smart_meal_planner.service.RecipeSearchRequest;
import smart_meal_planner.service.RecipeService;
import smart_meal_planner.service.PortionScalerService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.SQLException;
import java.util.List;

@RestController
public class RecipeController {

    private final RecipeService recipeService;
    private final PortionScalerService portionScalerService;

    public RecipeController(RecipeService recipeService, PortionScalerService portionScalerService) {
        this.recipeService = recipeService;
        this.portionScalerService = portionScalerService;
    }

    // Endpoint to search for recipes
    @PostMapping("/recipes")
    public MealPlan getRecipes(@RequestBody RecipeSearchRequest request) {
        return recipeService.findRecipeByString(request.getIngredients(), request.getMaxPrice());
    }

    // Endpoint to scale recipe portions
    @GetMapping("/recipes/{id}/scale")
    public List<Ingredient> scaleRecipe(
            @PathVariable int id,
            @RequestParam int servings
    ) throws SQLException {
        return portionScalerService.scaleRecipe(id, servings);
    }

    // Endpoint: recommend recipes similar to favorites
    @GetMapping("/recipes/recommend")
    public List<RecipeResult> recommendRecipes(@RequestParam List<Long> favoriteIds) {
        return recipeService.findSimilarRecipes(favoriteIds);
    }

}
