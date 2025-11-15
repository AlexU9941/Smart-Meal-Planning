package smart_meal_planner.model;

import jakarta.persistence.*;

@Entity
@Table(name = "meal_day")
public class MealDayEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "meal_plan_id")
    private MealPlanEntity mealPlan;

    @ManyToOne
    @JoinColumn(name = "lunch_recipe_id")
    private RecipeEntity lunch;

    @ManyToOne
    @JoinColumn(name = "dinner_recipe_id")
    private RecipeEntity dinner;

    public MealDayEntity() {}

    public MealDayEntity(RecipeEntity lunch, RecipeEntity dinner) {
        this.lunch = lunch;
        this.dinner = dinner;
    }

    public Long getId() { return id; }
    public RecipeEntity getLunch() { return lunch; }
    public RecipeEntity getDinner() { return dinner; }
    public void setMealPlan(MealPlanEntity mealPlan) { this.mealPlan = mealPlan; }
}
