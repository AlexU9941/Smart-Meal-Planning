package smart_meal_planner.nutrition;

import java.util.List;

public class Nutrition {
    private List<Nutrient> nutrients; 
    private CaloricBreakdown caloricBreakdown; 
    private WeightPerServing weightPerServing;
    
    public List<Nutrient> getNutrients() {
        return nutrients;
    }
    public void setNutrients(List<Nutrient> nutrients) {
        this.nutrients = nutrients;
    }
    public CaloricBreakdown getCaloricBreakdown() {
        return caloricBreakdown;
    }
    public void setCaloricBreakdown(CaloricBreakdown caloricBreakdown) {
        this.caloricBreakdown = caloricBreakdown;
    }
    public WeightPerServing getWeightPerServing() {
        return weightPerServing;
    }
    public void setWeightPerServing(WeightPerServing weightPerServing) {
        this.weightPerServing = weightPerServing;
    } 
}
