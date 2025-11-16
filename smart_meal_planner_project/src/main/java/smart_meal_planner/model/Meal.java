package smart_meal_planner.model;

import jakarta.persistence.*; // or javax.persistence if using older Spring Boot

@Entity
public class Meal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Embedded
    private NutritionInfo nutrition;

    public Meal() {}

    public Meal(String name, NutritionInfo nutrition) {
        this.name = name;
        this.nutrition = nutrition;
    }

    public NutritionInfo getNutrition() {
        return nutrition;
    }

    public void setNutrition(NutritionInfo nutrition) {
        this.nutrition = nutrition;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
