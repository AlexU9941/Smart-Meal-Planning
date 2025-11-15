package smart_meal_planner.service;

import smart_meal_planner.recipe.RecipeResult;
import java.util.List;

public class MealPlanRequest {

    private List<RecipeResult> lunchRecipes;
    private List<RecipeResult> dinnerRecipes;

    public List<RecipeResult> getLunchRecipes() { return lunchRecipes; }
    public List<RecipeResult> getDinnerRecipes() { return dinnerRecipes; }

    public void setLunchRecipes(List<RecipeResult> lunchRecipes) { this.lunchRecipes = lunchRecipes; }
    public void setDinnerRecipes(List<RecipeResult> dinnerRecipes) { this.dinnerRecipes = dinnerRecipes; }
}
