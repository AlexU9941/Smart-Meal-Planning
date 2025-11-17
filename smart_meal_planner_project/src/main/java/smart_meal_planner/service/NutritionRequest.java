package smart_meal_planner.service;

import smart_meal_planner.model.MealPlan;
import smart_meal_planner.model.UserNutritionalGoals;

public class NutritionRequest {
    private MealPlan mealPlan;
    private UserNutritionalGoals userGoals;

    public MealPlan getMealPlan() { return mealPlan; }
    public void setMealPlan(MealPlan mealPlan) { this.mealPlan = mealPlan; }

    public UserNutritionalGoals getUserGoals() { return userGoals; }
    public void setUserGoals(UserNutritionalGoals userGoals) { this.userGoals = userGoals; }
}
