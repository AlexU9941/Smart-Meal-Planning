package smart_meal_planner.recipe;

public class Nutrient {
    private String name; 
    private double amount; 
    private String unit;
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
    private double percentOfDailyNeeds; 

}
