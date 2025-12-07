package smart_meal_planner.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import smart_meal_planner.model.MealDay;
import smart_meal_planner.model.MealPlan;
import smart_meal_planner.model.RecipeEntity;
import smart_meal_planner.recipe.RecipeResult;
import smart_meal_planner.service.RecipeService;
import smart_meal_planner.repository.RecipeRepository;

@RestController
public class FindRecipesController {
    private final RecipeService recipeService;
    private final RecipeRepository recipeRepository;

    public FindRecipesController(RecipeService recipeService, RecipeRepository recipeRepository) {
        this.recipeService = recipeService;
        this.recipeRepository = recipeRepository;
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
        MealPlan mealPlan = recipeService.findRecipeByString(Collections.singletonList(name), budget);

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

    // Return all persisted recipes (used to populate available filter values)
    @GetMapping("/api/recipes/all")
    public List<SimpleRecipeDto> allRecipes() {
        List<RecipeEntity> all = recipeRepository.findAll();
        return all.stream().map(SimpleRecipeDto::from).collect(Collectors.toList());
    }

    // Filter recipes server-side using query parameters
    @GetMapping("/api/recipes/filter")
    public List<SimpleRecipeDto> filterRecipes(
            @RequestParam(value = "dietary", required = false) String dietary,
            @RequestParam(value = "ingredients", required = false) String ingredientsCsv,
            @RequestParam(value = "maxPrepTime", required = false) Integer maxPrepTime,
            @RequestParam(value = "difficulty", required = false) String difficulty,
            @RequestParam(value = "timeOfDay", required = false) String timeOfDay,
            @RequestParam(value = "maxCalories", required = false) Double maxCalories
    ) {
        List<RecipeEntity> all = recipeRepository.findAll();
        List<String> ingTemp = null;
        if (ingredientsCsv != null && !ingredientsCsv.isEmpty()) {
            ingTemp = java.util.Arrays.stream(ingredientsCsv.split(","))
                    .map(String::trim).map(String::toLowerCase)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
        }
        final List<String> ingredients = ingTemp;

        return all.stream().filter(r -> {
            // dietary: check dishTypes contains dietary
            if (dietary != null && !dietary.isEmpty()) {
                if (r.getDishTypes() == null || !r.getDishTypes().contains(dietary)) return false;
            }
            // ingredients: check any ingredient name contains provided ingredients
            if (ingredients != null && !ingredients.isEmpty()) {
                List<String> ingNames = r.getIngredients().stream().map(i -> i.getName().toLowerCase()).collect(Collectors.toList());
                boolean any = ingredients.stream().allMatch(req -> ingNames.stream().anyMatch(n -> n.contains(req)));
                if (!any) return false;
            }
            // maxPrepTime
            if (maxPrepTime != null) {
                if (r.getReadyInMinutes() > maxPrepTime) return false;
            }
            // difficulty inferred by readyInMinutes
            if (difficulty != null && !difficulty.isEmpty()) {
                String d = difficulty.toLowerCase();
                if (d.equals("easy") && r.getReadyInMinutes() > 30) return false;
                if (d.equals("medium") && r.getReadyInMinutes() > 60) return false;
                if (d.equals("hard") && r.getReadyInMinutes() <= 60) return false;
            }
            // timeOfDay: dishTypes contains it
            if (timeOfDay != null && !timeOfDay.isEmpty()) {
                if (r.getDishTypes() == null || !r.getDishTypes().contains(timeOfDay)) return false;
            }
            // maxCalories: check nutrition calories
            if (maxCalories != null) {
                if (r.getNutrition() == null || r.getNutrition().getCalories() > maxCalories) return false;
            }
            return true;
        }).map(SimpleRecipeDto::from).collect(Collectors.toList());
    }

    // DTO used by the search endpoint
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

    // Simple DTO for list/all/filter operations
    public static class SimpleRecipeDto {
        public long id;
        public String title;
        public List<String> dishTypes;
        public List<String> ingredients;
        public double readyInMinutes;
        public double calories;
        public double pricePerServing;

        public static SimpleRecipeDto from(RecipeEntity r) {
            SimpleRecipeDto d = new SimpleRecipeDto();
            d.id = r.getId();
            d.title = r.getTitle();
            d.dishTypes = r.getDishTypes();
            d.ingredients = r.getIngredients() == null ? new ArrayList<>() : r.getIngredients().stream().map(i -> i.getName()).collect(Collectors.toList());
            d.readyInMinutes = r.getReadyInMinutes();
            d.calories = r.getNutrition() == null ? 0.0 : r.getNutrition().getCalories();
            d.pricePerServing = r.getPricePerServing();
            return d;
        }
    }
}