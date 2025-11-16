package smart_meal_planner.service;

import org.springframework.stereotype.Service;
import smart_meal_planner.model.Meal;
import smart_meal_planner.model.NutritionInfo;
import smart_meal_planner.model.UserGoals;

import java.util.List;

@Service
public class NutritionService {

    public NutritionInfo calculateMealPlanNutrition(List<Meal> meals) {
        NutritionInfo totals = new NutritionInfo();
        for (Meal meal : meals) {
            NutritionInfo n = meal.getNutrition();
            if (n != null) {
                totals.setCalories(totals.getCalories() + n.getCalories());
                totals.setProtein(totals.getProtein() + n.getProtein());
                totals.setCarbs(totals.getCarbs() + n.getCarbs());
                totals.setFat(totals.getFat() + n.getFat());
                totals.setSugar(totals.getSugar() + n.getSugar());
                totals.setCholesterol(totals.getCholesterol() + n.getCholesterol());
            }
        }
        return totals;
    }

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