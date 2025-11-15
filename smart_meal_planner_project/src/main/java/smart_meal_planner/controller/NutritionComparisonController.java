package smart_meal_planner.controller;

import org.springframework.web.bind.annotation.*;
import smart_meal_planner.model.UserNutritionalGoals;
import smart_meal_planner.nutrition.NutritionComparison;
import smart_meal_planner.service.NutritionComparisonService;

@RestController
@RequestMapping("/api/nutrition")
public class NutritionComparisonController {

    private final NutritionComparisonService service;

    public NutritionComparisonController(NutritionComparisonService service) {
        this.service = service;
    }

    @PostMapping("/compare/{mealPlanId}")
    public NutritionComparison compare(
            @PathVariable Long mealPlanId,
            @RequestBody UserNutritionalGoals goals) {

        return service.compareMealPlan(mealPlanId, goals);
    }
}
