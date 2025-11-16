package smart_meal_planner.model;


import jakarta.persistence.*;
import smart_meal_planner.recipe.RecipeResult;

import java.util.Arrays;
import java.util.List;


@Entity
@Table(name = "recipes")
public class RecipeEntity {

    @Id
    private long id;  // Use Spoonacular id as primary key

    private String title;
    private String image;
    private String sourceUrl;

    private double readyInMinutes;
    private double cookingMinutes;
    private double preparationMinutes;

    private double servings;
    private double pricePerServing;

    @ElementCollection
    @CollectionTable(name = "recipe_dish_types", joinColumns = @JoinColumn(name = "recipe_id"))
    @Column(name = "dish_type")
    private List<String> dishTypes;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IngredientInput> ingredients;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "nutrition_id")
    private NutritionEntity nutrition;

    private int score;

    public RecipeEntity() {}

    // -----------------------------
    // Convenience conversion method
    // -----------------------------
    public static RecipeEntity fromRecipeResult(RecipeResult r) {
        RecipeEntity e = new RecipeEntity();
        e.id = r.getId();
        e.title = r.getTitle();
        e.image = r.getImage();
        e.sourceUrl = r.getSourceUrl();
        e.readyInMinutes = r.getReadyInMinutes();
        e.cookingMinutes = r.getCookingMinutes();
        e.preparationMinutes = r.getPreparationMinutes();
        e.servings = r.getServings();
        e.pricePerServing = r.getPricePerServing();
        e.dishTypes = Arrays.asList(r.getDishTypes());
        e.score = r.getScore();

        // Convert nested classes
        e.ingredients = IngredientInput.fromList(r.getExtendedIngredients(), e);
        e.nutrition = NutritionEntity.fromNutrition(r.getNutritionalInfo());

        return e;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public double getReadyInMinutes() {
        return readyInMinutes;
    }

    public void setReadyInMinutes(double readyInMinutes) {
        this.readyInMinutes = readyInMinutes;
    }

    public double getCookingMinutes() {
        return cookingMinutes;
    }

    public void setCookingMinutes(double cookingMinutes) {
        this.cookingMinutes = cookingMinutes;
    }

    public double getPreparationMinutes() {
        return preparationMinutes;
    }

    public void setPreparationMinutes(double preparationMinutes) {
        this.preparationMinutes = preparationMinutes;
    }

    public double getServings() {
        return servings;
    }

    public void setServings(double servings) {
        this.servings = servings;
    }

    public double getPricePerServing() {
        return pricePerServing;
    }

    public void setPricePerServing(double pricePerServing) {
        this.pricePerServing = pricePerServing;
    }

    public List<String> getDishTypes() {
        return dishTypes;
    }

    public void setDishTypes(List<String> dishTypes) {
        this.dishTypes = dishTypes;
    }

    public List<IngredientInput> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<IngredientInput> ingredients) {
        this.ingredients = ingredients;
    }

    public NutritionEntity getNutrition() {
        return nutrition;
    }

    public void setNutrition(NutritionEntity nutrition) {
        this.nutrition = nutrition;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public long getId() {
        return id;
    }

    public void setId(long id)
    {
        this.id = id; 
    }

    
}
