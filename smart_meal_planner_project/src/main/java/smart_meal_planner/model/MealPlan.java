package smart_meal_planner.model;

import java.util.List;

import jakarta.persistence.*;

import java.util.ArrayList;


import smart_meal_planner.recipe.RecipeResult;


@Entity
@Table(name = "meal_plan")
public class MealPlan {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToMany(mappedBy = "mealPlan", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<MealDay> days = new ArrayList<>();

    public MealPlan(){}


    public MealPlan(List<RecipeEntity> lunches, List<RecipeEntity> dinners) {
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
