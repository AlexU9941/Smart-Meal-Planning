package smart_meal_planner.model;
import jakarta.persistence.*;
import smart_meal_planner.nutrition.Nutrient;

import java.util.List; 

@Entity
@Table(name = "nutrients")
public class NutrientEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private double amount;
    private String unit;
    private double percentOfDailyNeeds;

    @ManyToOne
    @JoinColumn(name = "nutrition_id")
    private NutritionEntity nutrition;

    public NutrientEntity() {}

    public static NutrientEntity fromNutrient(Nutrient n, NutritionEntity parent) {
        NutrientEntity e = new NutrientEntity();
        e.name = n.getName();
        e.amount = n.getAmount();
        e.unit = n.getUnit();
        e.percentOfDailyNeeds = n.getPercentOfDailyNeeds();
        e.nutrition = parent;
        return e;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getPercentOfDailyNeeds() {
        return percentOfDailyNeeds;
    }

    public void setPercentOfDailyNeeds(double percentOfDailyNeeds) {
        this.percentOfDailyNeeds = percentOfDailyNeeds;
    }

    public NutritionEntity getNutrition() {
        return nutrition;
    }

    public void setNutrition(NutritionEntity nutrition) {
        this.nutrition = nutrition;
    }


    public Nutrient toNutrient() {
        Nutrient nutr = new Nutrient();
        nutr.setName(this.name);
        nutr.setAmount(this.amount);
        nutr.setUnit(this.unit);
        nutr.setPercentOfDailyNeeds(this.percentOfDailyNeeds);
        return nutr;
    }

}
