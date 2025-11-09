package smart_meal_planner.recipe;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

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

    @JsonProperty("extendedIngredients")
    private List<Ingredient> extendedIngredients; 

    @JsonProperty("nutrition")
    private Nutrition nutrition; 

  
    private int score; 

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecipeResult that = (RecipeResult) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }



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

    public int getScore() { return score; }

    public List<Ingredient> getExtendedIngredients() {
        return extendedIngredients;
    }

      public Nutrition getNutritionalInfo() {
        return nutrition;
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
    public void setScore(int score) { this.score = score; }
    public void setExtendedIngredients(List<Ingredient> extendedIngredients) {this.extendedIngredients = extendedIngredients; }
    public void setNutritionalInfo(Nutrition nutrition) {
        this.nutrition = nutrition;
    }
}
