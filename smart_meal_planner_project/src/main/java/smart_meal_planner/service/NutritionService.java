package smart_meal_planner.service;

import org.springframework.stereotype.Service;
//import smart_meal_planner.model.Meal; not a class
import smart_meal_planner.model.MealDay;
import smart_meal_planner.model.MealPlan;
import smart_meal_planner.model.NutritionInfo;
import smart_meal_planner.model.UserGoals;
import smart_meal_planner.model.UserNutritionalGoals;
import smart_meal_planner.nutrition.Nutrition;
import smart_meal_planner.nutrition.NutritionComparison;
import smart_meal_planner.model.RecipeEntity;

import java.util.List;

@Service
public class NutritionService {

   
    private final NutritionComparison nutritionComparison;

    public NutritionService() {
        this.nutritionComparison = new NutritionComparison();
    }

    /**
     * Compare a MealPlan against the user's nutritional goals.
     * Returns a 2D array: [day][nutrient] difference = goal - actual.
     */
    public double[][] compareMealPlanToGoals(MealPlan mealPlan, UserNutritionalGoals goals) {
        if (mealPlan == null || mealPlan.getDays() == null || mealPlan.getDays().isEmpty()) {
            throw new IllegalArgumentException("Meal plan is empty.");
        }
        if (goals == null) {
            throw new IllegalArgumentException("User nutritional goals are missing.");
        }

        return nutritionComparison.compareNutrients(mealPlan, goals);
    }
   

    // public NutritionInfo calculateMealPlanNutrition(List<Meal> meals) {
    //     NutritionInfo totals = new NutritionInfo();
    //     for (Meal meal : meals) {
    //         NutritionInfo n = meal.getNutrition();
    //         if (n != null) {
    //             totals.setCalories(totals.getCalories() + n.getCalories());
    //             totals.setProtein(totals.getProtein() + n.getProtein());
    //             totals.setCarbs(totals.getCarbs() + n.getCarbs());
    //             totals.setFat(totals.getFat() + n.getFat());
    //             totals.setSugar(totals.getSugar() + n.getSugar());
    //             totals.setCholesterol(totals.getCholesterol() + n.getCholesterol());
    //         }
    //     }
    //     return totals;
    // }
    
    public String evaluateHealthGoals(NutritionInfo weeklyTotals, UserGoals goals) {
        StringBuilder feedback = new StringBuilder();
        if (weeklyTotals.getCalories() > goals.getCalories()) {
            feedback.append("Your plan exceeds your calorie goal.\n");
        }
        if (weeklyTotals.getProtein() < goals.getProtein()) {
            feedback.append("Your plan is low in protein.\n");
        }
        if (weeklyTotals.getFat() > goals.getFat()) {
            feedback.append("Your plan has too much fat.\n");
        }
        return feedback.length() > 0 ? feedback.toString() : "Your plan meets your goals!";
    }
}