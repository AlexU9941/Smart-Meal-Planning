package smart_meal_planner.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import smart_meal_planner.dto.Ingredient;
import java.util.stream.Collectors;

@Entity
@Table(name = "ingredients") // this table name is fine
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

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public RecipeEntity getRecipe() {
        return recipe;
    }

    public void setRecipe(RecipeEntity recipe) {
        this.recipe = recipe;
    }


    @Override
    public String toString() {
        return "IngredientInput{" +
               "name='" + name + '\'' +
               ", quantity=" + quantity +
               ", unit='" + unit + '\'' +
               '}';
    }

   public static IngredientInput fromIngredient(Ingredient ing, RecipeEntity recipe) {
    IngredientInput i = new IngredientInput();
    i.setName(ing.getName());
    i.setQuantity(ing.getAmount());
    i.setUnit(ing.getUnit());
    i.setRecipe(recipe);
    return i;
}


    public static List<IngredientInput> fromList(List<Ingredient> ingredients, RecipeEntity recipe) {
       
        if (ingredients == null) {
        return new ArrayList<>();
        }
       
        return ingredients.stream()
                .map(ing -> IngredientInput.fromIngredient(ing, recipe))
                .collect(Collectors.toList());
    }

    public Ingredient toIngredient() {
        Ingredient ing = new Ingredient();
        ing.setName(this.name);
        ing.setAmount(this.quantity);
        ing.setUnit(this.unit);
        return ing;
    }

    public static List<Ingredient> toIngredientList(List<IngredientInput> inputs) {
        return inputs.stream()
                    .map(IngredientInput::toIngredient)
                    .collect(Collectors.toList());
    }


}
