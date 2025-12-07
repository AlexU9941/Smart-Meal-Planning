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
import smart_meal_planner.service.RecipeService;
import smart_meal_planner.repository.RecipeRepository;

@RestController
public class FindRecipesController {

    private final RecipeService recipeService;
    private final RecipeRepository recipeRepository;

    public FindRecipesController(RecipeService recipeService,
                                 RecipeRepository recipeRepository) {
        this.recipeService = recipeService;
        this.recipeRepository = recipeRepository;
    }

    // Example: GET /api/recipes/search?name=chicken&budget=20
    @GetMapping("/api/recipes/search")
    public List<FindRecipeDto> searchRecipes(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "budget", required = false) Double budget) {

        if (name == null) {
            name = "";
        }
        if (budget == null) {
            budget = 100.0;
        }

        MealPlan mealPlan = recipeService.findRecipeByString(
                Collections.singletonList(name),
                budget
        );

        List<FindRecipeDto> response = new ArrayList<>();
        if (mealPlan == null || mealPlan.getDays() == null) {
            return response;
        }

        // Deduplicate
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

    // Example: GET /api/recipes/all
    @GetMapping("/api/recipes/all")
    public List<SimpleRecipeDto> allRecipes() {
        List<RecipeEntity> all = recipeRepository.findAll();
        return all.stream()
                .map(SimpleRecipeDto::from)
                .collect(Collectors.toList());
    }

    // Example: GET /api/recipes/filter?dietary=vegan&maxPrepTime=30
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

        List<String> ingredientsTemp = null;
        if (ingredientsCsv != null && !ingredientsCsv.isEmpty()) {
            ingredientsTemp = java.util.Arrays.stream(ingredientsCsv.split(","))
                    .map(String::trim)
                    .map(String::toLowerCase)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
        }
        final List<String> ingredients = ingredientsTemp;

        return all.stream()
                .filter(r -> {

                    // dietary → dishTypes contains it
                    if (dietary != null && !dietary.isEmpty()) {
                        if (r.getDishTypes() == null || !r.getDishTypes().contains(dietary)) {
                            return false;
                        }
                    }

                    // ingredients filter
                    if (ingredients != null && !ingredients.isEmpty()) {
                        List<String> rIng = r.getIngredients().stream()
                                .map(i -> i.getName().toLowerCase())
                                .collect(Collectors.toList());

                        boolean allMatch = ingredients.stream()
                                .allMatch(req -> rIng.stream().anyMatch(n -> n.contains(req)));

                        if (!allMatch) {
                            return false;
                        }
                    }

                    // max prep time
                    if (maxPrepTime != null) {
                        if (r.getReadyInMinutes() > maxPrepTime) {
                            return false;
                        }
                    }

                    // difficulty by readyInMinutes
                    if (difficulty != null && !difficulty.isEmpty()) {
                        String d = difficulty.toLowerCase();

                        if (d.equals("easy") && r.getReadyInMinutes() > 30) return false;
                        if (d.equals("medium") && r.getReadyInMinutes() > 60) return false;
                        if (d.equals("hard") && r.getReadyInMinutes() <= 60) return false;
                    }

                    // time of day
                    if (timeOfDay != null && !timeOfDay.isEmpty()) {
                        if (r.getDishTypes() == null || !r.getDishTypes().contains(timeOfDay)) {
                            return false;
                        }
                    }

                    // calories
                    if (maxCalories != null) {
                        if (r.getNutrition() == null ||
                                r.getNutrition().getCalories() > maxCalories) {
                            return false;
                        }
                    }

                    return true;
                })
                .map(SimpleRecipeDto::from)
                .collect(Collectors.toList());
    }

    // DTO for search endpoint
    public static class FindRecipeDto {
        private long id;
        private String name;
        private double budget;

        public FindRecipeDto(RecipeEntity r) {
            this.id = r.getId();
            this.name = r.getTitle();
            this.budget = r.getPricePerServing();
        }

        public long getId() { return id; }
        public String getName() { return name; }
        public double getBudget() { return budget; }

        public void setId(long id) { this.id = id; }
        public void setName(String name) { this.name = name; }
        public void setBudget(double budget) { this.budget = budget; }
    }

    // DTO for filter & all endpoints
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
            d.ingredients = r.getIngredients() == null
                    ? new ArrayList<String>()
                    : r.getIngredients().stream()
                        .map(i -> i.getName())
                        .collect(Collectors.toList());

            d.readyInMinutes = r.getReadyInMinutes();
            d.calories = r.getNutrition() != null ? r.getNutrition().getCalories() : 0.0;
            d.pricePerServing = r.getPricePerServing();

            return d;
        }
    }
}
