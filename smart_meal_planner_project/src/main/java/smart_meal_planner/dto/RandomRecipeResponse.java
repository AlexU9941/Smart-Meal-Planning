package smart_meal_planner.dto;

import java.util.List;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import smart_meal_planner.recipe.RecipeResult;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RandomRecipeResponse {

    private List<RecipeResult> recipes;

    public List<RecipeResult> getRecipes() {
        return recipes;
    }

    public void setRecipes(List<RecipeResult> recipes) {
        this.recipes = recipes;
    }
}
