package smart_meal_planner.recipe;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RecipeResult {
    private int id;
    private String title;
    private String image;
    private String sourceUrl; 

    private double readyInMinutes;
    private double cookingMinutes; 
    private double preparationMinutes;

    private double servings; 
    private double pricePerServing;

    private String[] dishTypes; 

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public double getReadyInMinutes() {
        return readyInMinutes;
    }

    public double getCookingMinutes() {
        return cookingMinutes;
    }

    public double getPreparationMinutes() {
        return preparationMinutes;
    }

    public double getServings() {
        return servings;
    }

    public double getPricePerServing() {
        return pricePerServing;
    }

    public String[] getDishTypes() {
        return dishTypes;
    }

  

    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setImage(String image) { this.image = image; }
    public void setSourceUrl(String sourceUrl) { this.sourceUrl = sourceUrl; }
    public void setReadyInMinutes(double readyInMinutes) { this.readyInMinutes = readyInMinutes; }
    public void setCookingMinutes(double cookingMinutes) { this.cookingMinutes = cookingMinutes; }
    public void setPreparationMinutes(double preparationMinutes) { this.preparationMinutes = preparationMinutes; }
    public void setServings(double servings) { this.servings = servings; }
    public void setPricePerServing(double pricePerServing) { this.pricePerServing = pricePerServing; }
    public void setDishTypes(String[] dishTypes) { this.dishTypes = dishTypes; }
}
