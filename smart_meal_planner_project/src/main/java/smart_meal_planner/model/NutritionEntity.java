package smart_meal_planner.model;

import jakarta.persistence.*;
import smart_meal_planner.nutrition.CaloricBreakdown;
import smart_meal_planner.nutrition.Nutrient;
import smart_meal_planner.nutrition.Nutrition;
import smart_meal_planner.nutrition.WeightPerServing;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "nutrition")
public class NutritionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @OneToMany(mappedBy = "nutrition", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NutrientEntity> nutrients = new ArrayList<NutrientEntity>();

    private Double percentCarbs;
    private Double percentProtein;
    private Double percentFat;

    private Double amount;
    private String unit;

    public NutritionEntity() {
    }

    public static NutritionEntity fromNutrition(Nutrition n) {
        if (n == null) {
            return null;
        }

        NutritionEntity e = new NutritionEntity();

        if (n.getNutrients() != null) {
            e.nutrients = n.getNutrients().stream()
                    .map(nutr -> NutrientEntity.fromNutrient(nutr, e))
                    .collect(Collectors.toList());
        }

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

    public void setNutrients(List<NutrientEntity> nutrients) {
        this.nutrients = nutrients;
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

        if (this.nutrients != null) {
            n.setNutrients(this.nutrients.stream()
                    .map(NutrientEntity::toNutrient)
                    .collect(Collectors.toList()));
        }

        if (percentCarbs != null || percentProtein != null || percentFat != null) {
            CaloricBreakdown cb = new CaloricBreakdown();
            cb.setPercentCarbs(percentCarbs);
            cb.setPercentProtein(percentProtein);
            cb.setPercentFat(percentFat);
            n.setCaloricBreakdown(cb);
        }

        if (amount != null && unit != null) {
            WeightPerServing wps = new WeightPerServing();
            wps.setAmount(amount);
            wps.setUnit(unit);
            n.setWeightPerServing(wps);
        }

        return n;
    }

    private Double findNutrientAmount(String key) {
        if (nutrients == null) {
            return null;
        }

        for (NutrientEntity n : nutrients) {
            if (n.getName() != null && n.getName().toLowerCase().contains(key)) {
                return n.getAmount();
            }
        }
        return null;
    }

    public Double getCalories() {
        return findNutrientAmount("calorie");
    }

    public Double getProtein() {
        return findNutrientAmount("protein");
    }

    public Double getFat() {
        return findNutrientAmount("fat");
    }

    public Double getCarbs() {
        return findNutrientAmount("carbohydrate");
    }
}
