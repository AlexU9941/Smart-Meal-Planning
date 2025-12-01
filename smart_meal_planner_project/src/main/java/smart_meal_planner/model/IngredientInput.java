package smart_meal_planner.model;

import jakarta.persistence.*;
import smart_meal_planner.dto.Ingredient;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "ingredients")
public class IngredientInput {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private double quantity;
    private String unit;

    @ManyToOne
    @JoinColumn(name = "recipe_id")
    private RecipeEntity recipe;

    @ManyToOne
    private User user;

    public IngredientInput() {}

    public IngredientInput(String name, double quantity, String unit) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }

    // -----------------------------
    // Getters & Setters
    // -----------------------------
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public RecipeEntity getRecipe() { return recipe; }
    public void setRecipe(RecipeEntity recipe) { this.recipe = recipe; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    @Override
    public String toString() {
        return "IngredientInput{" +
               "name='" + name + '\'' +
               ", quantity=" + quantity +
               ", unit='" + unit + '\'' +
               '}';
    }

    // -----------------------------
    // Conversion: DTO → Entity
    // -----------------------------
    public static IngredientInput fromIngredient(Ingredient ing, RecipeEntity recipe) {
        IngredientInput i = new IngredientInput();
        i.setName(ing.getName());
        i.setQuantity(ing.getAmount());
        i.setUnit(ing.getUnit());
        i.setRecipe(recipe);
        return i;
    }

    public static List<IngredientInput> fromList(List<Ingredient> ingredients, RecipeEntity recipe) {
        if (ingredients == null) return new ArrayList<>();
        return ingredients.stream()
                .map(ing -> fromIngredient(ing, recipe))
                .collect(Collectors.toList());
    }

    // -----------------------------
    // Conversion: Entity → DTO
    // -----------------------------
    public Ingredient toIngredient() {
        return new Ingredient(this.name, this.quantity, this.unit);
    }

    public static List<Ingredient> toIngredientList(List<IngredientInput> inputs) {
        return inputs.stream()
                .map(IngredientInput::toIngredient)
                .collect(Collectors.toList());
    }
}
