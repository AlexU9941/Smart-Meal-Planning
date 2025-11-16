package smart_meal_planner.model;
import jakarta.persistence.*;
import smart_meal_planner.recipe.RecipeResult;

@Entity
@Table(name = "meal_day")
public class MealDay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "meal_plan_id")
    private MealPlan mealPlan;

    @ManyToOne(cascade = CascadeType.ALL)
    private RecipeEntity lunch;
    
    @ManyToOne(cascade = CascadeType.ALL)
    private RecipeEntity dinner;

    public MealDay() {}

    public MealDay(RecipeEntity lunch, RecipeEntity dinner) {
        this.lunch = lunch;
        this.dinner = dinner;
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

    public void setMealPlan(MealPlan mealPlan) {
        this.mealPlan = mealPlan;
    }
     
}
