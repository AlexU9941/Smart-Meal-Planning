//TEMPORARILY COMMENTED OUT DUE TO ERRORS
package smart_meal_planner.controller;

import org.springframework.web.bind.annotation.*;
import smart_meal_planner.recipe.RecipeResult;
import smart_meal_planner.service.RecipeFilterService;

import java.util.List;

@RestController
@RequestMapping("/api/recipes")
public class RecipeFilterController {

    private final RecipeFilterService filterService;

    public RecipeFilterController(RecipeFilterService filterService) {
        this.filterService = filterService;
    }

    @PostMapping("/filter")
    public List<RecipeResult> filterRecipes(@RequestBody FilterRequest request) {
        List<RecipeResult> filtered = filterService.filterRecipes(
                request.getRecipes(),
                request.getDietaryRestriction(),
                request.getIngredients(),
                request.getMaxPrepTime(),
                request.getDifficulty(),
                request.getTimeOfDay(),
                request.getMaxCalories()
        );

        if (filtered.isEmpty()) {
            throw new IllegalArgumentException("No recipes match the selected filters.");
        }
        return filtered;
    }

    public static class FilterRequest {
        private List<RecipeResult> recipes;
        private String dietaryRestriction;
        private List<String> ingredients;
        private Integer maxPrepTime;
        private String difficulty;
        private String timeOfDay;
        private Double maxCalories;

        // Getters and Setters
        public List<RecipeResult> getRecipes() { return recipes; }
        public void setRecipes(List<RecipeResult> recipes) { this.recipes = recipes; }

        public String getDietaryRestriction() { return dietaryRestriction; }
        public void setDietaryRestriction(String dietaryRestriction) { this.dietaryRestriction = dietaryRestriction; }

        public List<String> getIngredients() { return ingredients; }
        public void setIngredients(List<String> ingredients) { this.ingredients = ingredients; }

        public Integer getMaxPrepTime() { return maxPrepTime; }
        public void setMaxPrepTime(Integer maxPrepTime) { this.maxPrepTime = maxPrepTime; }

        public String getDifficulty() { return difficulty; }
        public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

        public String getTimeOfDay() { return timeOfDay; }
        public void setTimeOfDay(String timeOfDay) { this.timeOfDay = timeOfDay; }

        public Double getMaxCalories() { return maxCalories; }
        public void setMaxCalories(Double maxCalories) { this.maxCalories = maxCalories; }
    }
}
