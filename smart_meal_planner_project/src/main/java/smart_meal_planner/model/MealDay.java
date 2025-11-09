package smart_meal_planner.model;
import smart_meal_planner.recipe.RecipeResult;

public class MealDay {
    private RecipeResult lunch;
    private RecipeResult dinner;

    public MealDay(RecipeResult lunch, RecipeResult dinner) {
        this.lunch = lunch;
        this.dinner = dinner;
    }

    public RecipeResult getLunch() {
        return lunch;
    }

    public RecipeResult getDinner() {
        return dinner;
    }
     
}
