package smart_meal_planner.service;

import org.springframework.stereotype.Service;
import smart_meal_planner.model.MealPlanEntity;
import smart_meal_planner.model.MealDayEntity;
import smart_meal_planner.model.RecipeEntity;
import smart_meal_planner.model.UserNutritionalGoals;
import smart_meal_planner.nutrition.NutrientValue;
import smart_meal_planner.nutrition.NutritionComparison;

import java.util.*;

@Service
public class NutritionComparisonService {

    private final MealPlanService mealPlanService;

    public NutritionComparisonService(MealPlanService mealPlanService) {
        this.mealPlanService = mealPlanService;
    }

    public NutritionComparison compareMealPlan(Long mealPlanId, UserNutritionalGoals goals) {

        MealPlanEntity plan = mealPlanService.getMealPlan(mealPlanId);
        if (plan == null) return null;

        Map<String, Double> totalConsumed = new HashMap<>();

        // Sum nutrients across all meals
        for (MealDayEntity day : plan.getDays()) {
            accumulate(day.getLunch(), totalConsumed);
            accumulate(day.getDinner(), totalConsumed);
        }

        // Build comparison result
        NutritionComparison comparison = new NutritionComparison();

        for (NutritionComparison.TrackedNutrient nutrient : NutritionComparison.TrackedNutrient.values()) {
            double goal = goals.getGoal(nutrient);
            double consumed = totalConsumed.getOrDefault(nutrient.getSpoonName(), 0.0);
            comparison.add(nutrient, goal, consumed);
        }

        return comparison;
    }

    private void accumulate(RecipeEntity recipe, Map<String, Double> totals) {
        if (recipe == null || recipe.getNutrition() == null) return;

        for (NutrientValue n : recipe.getNutrition()) {
            totals.merge(n.getName(), n.getAmount(), Double::sum);
        }
    }
}
