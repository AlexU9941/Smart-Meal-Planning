package smart_meal_planner.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "meal_plan")
public class MealPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // A meal plan contains 7 MealDay objects (Sunday–Saturday)
    @OneToMany(mappedBy = "mealPlan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MealDay> days = new ArrayList<>();

    @Column(name = "user_id")
    private Long userId;

    public MealPlan() {}

    // ---------- GETTERS ----------

    public Long getId() {
        return id;
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

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    // Helper method to add a day to the plan
    public void addDay(MealDay day) {
        day.setMealPlan(this);
        this.days.add(day);
    }
}