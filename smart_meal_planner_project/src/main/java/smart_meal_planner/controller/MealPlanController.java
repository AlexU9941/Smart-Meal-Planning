package smart_meal_planner.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import smart_meal_planner.dto.MealPlanDTO;
import smart_meal_planner.model.MealPlan;
import smart_meal_planner.model.UserNutritionalGoals;
import smart_meal_planner.service.MealPlanService;
import smart_meal_planner.service.RecipeService;
import smart_meal_planner.service.GenerateRequest;


@RestController
@RequestMapping("/meal-plans")
public class MealPlanController {

    private final MealPlanService mealPlanService;
    private final RecipeService recipeService;

    public MealPlanController(MealPlanService mealPlanService, RecipeService recipeService) {
        this.mealPlanService = mealPlanService;
        this.recipeService = recipeService;
    }

    @PostMapping
    public MealPlan createMealPlan(@RequestBody MealPlanDTO dto) {
        return mealPlanService.saveMealPlan(dto);
    }

    @GetMapping("/{id}")
    public MealPlan getMealPlan(@PathVariable Long id) {
        return mealPlanService.getMealPlan(id);
    }

    @PostMapping("/{id}/nutrition-diff")
    public double[][] getNutritionDiff(@PathVariable Long id, @RequestBody UserNutritionalGoals goals) {
        return mealPlanService.compareNutrition(id, goals);
    }

    // @PostMapping("/generate")
    // public MealPlan generateMealPlan(@RequestBody GenerateRequest request) {
    //    // MealPlan plan = recipeService.findRecipeByIngredients(request.getIngredients(), request.getBudget());
    //    MealPlan plan = recipeService.findRecipeByString(request.getIngredients(), request.getBudget());
      
    //     return plan; 
    // }

    @PostMapping("/generate")
    public MealPlan generateMealPlan(@RequestBody GenerateRequest request) {
        double budget = request.getBudget() != null ? request.getBudget() : 100.0;
        List<String> ingredients = request.getIngredients() != null ? request.getIngredients() : Arrays.asList("chicken","beef","vegetables");
        
        try {
            MealPlan plan = recipeService.findRecipeByString(ingredients, budget);
            return plan;
        } catch (Exception e) {
            // log error and return empty MealPlan
            e.printStackTrace();
            return new MealPlan(); // make sure MealPlan constructor handles empty days
        }
    }
}

