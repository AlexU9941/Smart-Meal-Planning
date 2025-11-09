package smart_meal_planner.model;

import jakarta.persistence.*;

@Entity
@Table(name = "ingredients") // this table name is fine
public class IngredientInput {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int quantity;
    private String unit;

    public IngredientInput() {}

    public IngredientInput(String name, int quantity, String unit) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    @Override
    public String toString() {
        return "IngredientInput{" +
               "name='" + name + '\'' +
               ", quantity=" + quantity +
               ", unit='" + unit + '\'' +
               '}';
    }
}
