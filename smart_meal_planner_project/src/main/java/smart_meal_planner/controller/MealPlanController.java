package smart_meal_planner.controller;

import org.springframework.web.bind.annotation.*;
import smart_meal_planner.model.MealPlanEntity;
import smart_meal_planner.recipe.RecipeResult;
import smart_meal_planner.service.MealPlanService;

import java.util.List;

@RestController
@RequestMapping("/api/mealplan")
public class MealPlanController {

    private final MealPlanService mealPlanService;

    public MealPlanController(MealPlanService mealPlanService) {
        this.mealPlanService = mealPlanService;
    }

    @PostMapping
    public MealPlanEntity generateMealPlan(@RequestBody MealPlanRequest request) {
        return mealPlanService.createMealPlan(request.getLunchRecipes(), request.getDinnerRecipes());
    }

    @GetMapping("/{id}")
    public MealPlanEntity getMealPlan(@PathVariable Long id) {
        return mealPlanService.getMealPlan(id);
    }
}
