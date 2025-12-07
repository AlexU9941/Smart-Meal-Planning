package smart_meal_planner.model;

import jakarta.persistence.*;

@Entity
@Table(name = "meal_day")
public class MealDay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "day")
    private String day;

    @ManyToOne
    @JoinColumn(name = "breakfast_id")
    private RecipeEntity breakfast;

    @ManyToOne
    @JoinColumn(name = "lunch_id")
    private RecipeEntity lunch;

    @ManyToOne
    @JoinColumn(name = "dinner_id")
    private RecipeEntity dinner;

    @ManyToOne
    @JoinColumn(name = "meal_plan_id")
    private MealPlan mealPlan;

    public Long getId() {
        return id;
    }

    public String getDay() {
        return day;
    }

    public RecipeEntity getBreakfast() {
        return breakfast;
    }

    public RecipeEntity getLunch() {
        return lunch;
    }

    public RecipeEntity getDinner() {
        return dinner;
    }

    public MealPlan getMealPlan() {
        return mealPlan;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public void setBreakfast(RecipeEntity breakfast) {
        this.breakfast = breakfast;
    }

    public void setLunch(RecipeEntity lunch) {
        this.lunch = lunch;
    }

    public void setDinner(RecipeEntity dinner) {
        this.dinner = dinner;
    }

    public void setMealPlan(MealPlan mealPlan) {
        this.mealPlan = mealPlan;
    }
}
