package smart_meal_planner.model;

import jakarta.persistence.*;
import smart_meal_planner.nutrition.Nutrition;
import java.util.stream.Collectors;

import java.util.List; 
@Entity
@Table(name = "nutrition")
public class NutritionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // One-to-many because each recipe has many individual nutrients
    @OneToMany(mappedBy = "nutrition", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NutrientEntity> nutrients;

    // store caloric breakdown if needed
    private Double percentCarbs;
    private Double percentProtein;
    private Double percentFat;

    // Store weight per serving if needed
    private Double amount;
    private String unit;

    public NutritionEntity() {}

    public static NutritionEntity fromNutrition(Nutrition n) {
        if (n == null) return null;

        NutritionEntity e = new NutritionEntity();

        // Nutrients list
        e.nutrients = n.getNutrients()
                       .stream()
                       .map(nutr -> NutrientEntity.fromNutrient(nutr, e))
                       .collect(Collectors.toList());


        // Optional nutrition sub-objects
        if (n.getCaloricBreakdown() != null) {
            e.percentCarbs = n.getCaloricBreakdown().getPercentCarbs();
            e.percentProtein = n.getCaloricBreakdown().getPercentProtein();
            e.percentFat = n.getCaloricBreakdown().getPercentFat();
        }

        if (n.getWeightPerServing() != null) {
            e.amount = n.getWeightPerServing().getAmount();
            e.unit = n.getWeightPerServing().getUnit();
        }

        return e;
    }

    public List<NutrientEntity> getNutrients() {
        return nutrients;
    }

    public Double getPercentCarbs() {
        return percentCarbs;
    }

    public void setPercentCarbs(Double percentCarbs) {
        this.percentCarbs = percentCarbs;
    }

    public Double getPercentProtein() {
        return percentProtein;
    }

    public void setPercentProtein(Double percentProtein) {
        this.percentProtein = percentProtein;
    }

    public Double getPercentFat() {
        return percentFat;
    }

    public void setPercentFat(Double percentFat) {
        this.percentFat = percentFat;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    
}
