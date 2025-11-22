package smart_meal_planner.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import smart_meal_planner.model.MealDay;
import smart_meal_planner.model.MealPlan;
import smart_meal_planner.model.RecipeEntity;
import smart_meal_planner.recipe.RecipeResult;
import smart_meal_planner.service.RecipeService;

@RestController
public class FindRecipesController {
    private final RecipeService recipeService;

    public FindRecipesController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    // Matches frontend call: GET /api/recipes/search?name=...&budget=...
    @GetMapping("/api/recipes/search")
    public List<FindRecipeDto> searchRecipes(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "budget", required = false) Double budget) {

        // Defensive defaults: name -> empty string, budget -> large default
        if (name == null) name = "";
        if (budget == null) budget = 100.0; // default max price if not provided

        // Use existing service method by passing the search name as the single ingredient.
        MealPlan mealPlan = recipeService.findRecipeByIngredients(Collections.singletonList(name), budget);

        List<FindRecipeDto> response = new ArrayList<>();
        if (mealPlan == null || mealPlan.getDays() == null) {
            return response;
        }

        // Deduplicate results by id while flattening lunches and dinners
        Set<Long> seen = new HashSet<>();
        for (MealDay day : mealPlan.getDays()) {
            if (day.getLunch() != null) {
                RecipeEntity r = day.getLunch();
                if (!seen.contains(r.getId())) {
                    seen.add(r.getId());
                    response.add(new FindRecipeDto(r));
                }
            }
            if (day.getDinner() != null) {
                RecipeEntity r = day.getDinner();
                if (!seen.contains(r.getId())) {
                    seen.add(r.getId());
                    response.add(new FindRecipeDto(r));
                }
            }
        }

        return response;
    }

    // Simple DTO tailored to the frontend: { id, name, budget }
    public static class FindRecipeDto {
        private long id;
        private String name;
        private double budget;

        public FindRecipeDto(RecipeEntity r) {
            this.id = r.getId();
            this.name = r.getTitle();
            this.budget = r.getPricePerServing();
        }

        public Long getId() { return id; }
        public String getName() { return name; }
        public double getBudget() { return budget; }

        public void setId(int id) { this.id = id; }
        public void setName(String name) { this.name = name; }
        public void setBudget(double budget) { this.budget = budget; }
    }
}

