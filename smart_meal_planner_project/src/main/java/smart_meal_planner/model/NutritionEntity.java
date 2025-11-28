package smart_meal_planner.model;

import jakarta.persistence.*;
import smart_meal_planner.nutrition.CaloricBreakdown;
import smart_meal_planner.nutrition.Nutrient;
import smart_meal_planner.nutrition.Nutrition;
import smart_meal_planner.nutrition.WeightPerServing;

import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List; 
@Entity
@Table(name = "nutrition")
public class NutritionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // One-to-many because each recipe has many individual nutrients
    @JsonIgnore
    @OneToMany(mappedBy = "nutrition", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NutrientEntity> nutrients = new ArrayList<>();

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

    public Nutrition toNutrition() {
    Nutrition n = new Nutrition();

    // Convert nutrients
    if (this.nutrients != null) {
        n.setNutrients(this.nutrients.stream()
            .map(NutrientEntity::toNutrient)  // you need a method in NutrientEntity
            .collect(Collectors.toList())
        );
    }

    // Caloric breakdown
    if (percentCarbs != null || percentProtein != null || percentFat != null) {
        CaloricBreakdown cb = new CaloricBreakdown();
        cb.setPercentCarbs(percentCarbs);
        cb.setPercentProtein(percentProtein);
        cb.setPercentFat(percentFat);
        n.setCaloricBreakdown(cb);
    }

    // Weight per serving
    if (amount != null && unit != null) {
        WeightPerServing wps = new WeightPerServing();
        wps.setAmount(amount);
        wps.setUnit(unit);
        n.setWeightPerServing(wps);
    }

    return n;
}

    public Double getCalories() {
    if (nutrients == null) return null;

    return nutrients.stream()
            .filter(n -> n.getName().equalsIgnoreCase("Calories"))
            .map(NutrientEntity::getAmount)
            .findFirst()
            .orElse(null);
    }

}
