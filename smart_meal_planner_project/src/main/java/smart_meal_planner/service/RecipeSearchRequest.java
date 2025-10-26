package smart_meal_planner.service;

import java.util.List;

public class RecipeSearchRequest {
    private List<String> ingredients;
    private double maxPrice;

    // Getters and setters
    public List<String> getIngredients() { return ingredients; }
    public void setIngredients(List<String> ingredients) { this.ingredients = ingredients; }

    public double getMaxPrice() { return maxPrice; }
    public void setMaxPrice(double maxPrice) { this.maxPrice = maxPrice; }
}
