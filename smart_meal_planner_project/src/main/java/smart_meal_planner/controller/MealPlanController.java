package smart_meal_planner.controller;

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

    @PostMapping("/generate")
    public MealPlan generateMealPlan(@RequestBody GenerateRequest request) {
        MealPlan plan = recipeService.findRecipeByIngredients(request.getIngredients(), request.getBudget());
        return plan; 
    }
}

