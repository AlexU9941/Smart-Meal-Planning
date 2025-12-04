package smart_meal_planner.service;
import java.util.List;

import smart_meal_planner.dto.Ingredient;

public class GenerateRequest {

    // private List<Ingredient> ingredients;
    private List<String> ingredients;
    private Double budget;

    // 🔥 NEW — attach generated meal plan to a specific user
    private Long userId;

    // public List<Ingredient> getIngredients() {
    //     return ingredients;
    // }

    // public void setIngredients(List<Ingredient> ingredients) {
    //     this.ingredients = ingredients;
    // }

    public Double getBudget() {
        return budget;
    }

    public void setBudget(Double budget) {
        this.budget = budget;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    // 🔥 NEW — getter for userId
    public Long getUserId() {
        return userId;
    }

    // 🔥 NEW — setter for userId
    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
