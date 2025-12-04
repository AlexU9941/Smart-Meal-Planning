package smart_meal_planner.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "meal_day")
public class MealDay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "day")
    private String day;   // <-- REQUIRED

    @ManyToOne
    @JoinColumn(name = "breakfast_id")
    private RecipeEntity breakfast;

    // 🔥 NEW — breakfast support
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "breakfast_id")
    private RecipeEntity breakfast;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "lunch_id")
    private RecipeEntity lunch;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "dinner_id")
    private RecipeEntity dinner;

    @ManyToOne
    @JoinColumn(name = "meal_plan_id")
    private MealPlan mealPlan;

    // ---------- GETTERS ----------
    public Long getId() {
        return id;
    }

    public MealDay(RecipeEntity breakfast, RecipeEntity lunch, RecipeEntity dinner) {
        this.breakfast = breakfast;
        this.lunch = lunch;
        this.dinner = dinner;
    }

    public RecipeEntity getBreakfast() {
        return breakfast;
    }

    public void setBreakfast(RecipeEntity breakfast) {
        this.breakfast = breakfast;
    }

    public RecipeEntity getLunch() {
        return lunch;
    }

    public void setLunch(RecipeEntity lunch) {
        this.lunch = lunch;
    }

    public RecipeEntity getDinner() {
        return dinner;
    }

    public void setDinner(RecipeEntity dinner) {
        this.dinner = dinner;
    }

    public MealPlan getMealPlan() {
        return mealPlan;
    }

    // ---------- SETTERS ----------
    public void setId(Long id) {
        this.id = id;
    }

    public void setLunch(RecipeEntity lunch) {
        this.lunch = lunch;
    }

    public void setDinner(RecipeEntity dinner) {
        this.dinner = dinner;
    }
}
