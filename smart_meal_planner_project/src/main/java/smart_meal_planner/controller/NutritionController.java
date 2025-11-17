
package smart_meal_planner.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import smart_meal_planner.model.MealPlan;
//import smart_meal_planner.model.Meal; not an actual class 
import smart_meal_planner.model.NutritionInfo;
import smart_meal_planner.model.UserGoals;
import smart_meal_planner.nutrition.Nutrition;
import smart_meal_planner.service.NutritionRequest;
import smart_meal_planner.service.NutritionService;

import java.util.List;

@RestController
@RequestMapping("/api/nutrition")
public class NutritionController {

    @Autowired
    private NutritionService nutritionService;

    //@PostMapping("/track")
    // public NutritionInfo trackNutrition(@RequestBody List<Meal> mealPlan) {
    //     if (mealPlan == null || mealPlan.isEmpty()) {
    //         throw new IllegalArgumentException("No meal plan provided.");
    //     }
    //     return nutritionService.calculateMealPlanNutrition(mealPlan);
    // }
    
    @PostMapping("/track")
    public double[][] getNutritionDiff(@RequestBody NutritionRequest request) {
        return nutritionService.compareMealPlanToGoals(request.getMealPlan(), request.getUserGoals());
    }

    @PostMapping("/health-check")
    public String healthCheck(@RequestBody HealthCheckRequest request) {
        if (request.getWeeklyTotals() == null) {
            throw new IllegalArgumentException("Weekly totals missing.");
        }
        return nutritionService.evaluateHealthGoals(request.getWeeklyTotals(), request.getUserGoals());
    }

    // Inner class for request payload
    public static class HealthCheckRequest {
        private NutritionInfo weeklyTotals;
        private UserGoals userGoals;

        public NutritionInfo getWeeklyTotals() { return weeklyTotals; }
        public void setWeeklyTotals(NutritionInfo weeklyTotals) { this.weeklyTotals = weeklyTotals; }

        public UserGoals getUserGoals() { return userGoals; }
        public void setUserGoals(UserGoals userGoals) { this.userGoals = userGoals; }
    }
}
