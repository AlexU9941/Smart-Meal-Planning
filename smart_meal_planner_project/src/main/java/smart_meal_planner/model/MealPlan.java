package smart_meal_planner.model;

import java.util.List;
import java.util.ArrayList;


import smart_meal_planner.recipe.RecipeResult;

public class MealPlan {
    List<MealDay> days = new ArrayList<>();

    
    public MealPlan(List<RecipeResult> lunches, List<RecipeResult> dinners) {
        int lunchCount = Math.min(7, lunches.size());
        int dinnerCount = Math.min(7, dinners.size());

        int count = Math.min(lunchCount, dinnerCount);
        
        for (int i = 0; i < count; i++) {
            days.add(new MealDay(lunches.get(i), dinners.get(i)));
        }
    }

    public List<MealDay> getDays() {
        return days;
    }


    public String printMealPlan() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < days.size(); i++) {
            MealDay day = days.get(i);
            sb.append("Day ").append(i + 1).append(":\n");
            sb.append("  Lunch: ").append(day.getLunch().getTitle()).append("\n");
            sb.append("  Dinner: ").append(day.getDinner().getTitle()).append("\n");
        }
        return sb.toString();
    }
}
