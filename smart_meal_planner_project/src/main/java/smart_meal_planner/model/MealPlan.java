package smart_meal_planner.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.ArrayList;

@Entity
@Table(name = "meal_plan")
public class MealPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    public MealPlan(){}

    // 🔥 UPDATED to accept breakfast meals too
    public MealPlan(List<RecipeEntity> breakfasts, List<RecipeEntity> lunches, List<RecipeEntity> dinners) {

        int size = Math.min(7, Math.min(breakfasts.size(), Math.min(lunches.size(), dinners.size())));

        for (int i = 0; i < size; i++) {
            days.add(new MealDay(
                breakfasts.get(i),
                lunches.get(i),
                dinners.get(i)
            ));
        }
    }

    public List<MealDay> getDays() {
        return days;
    }

    public Long getUserId() {
        return userId;
    }

    // ---------- SETTERS ----------

    public void setDays(List<MealDay> days) {
        this.days = days;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
