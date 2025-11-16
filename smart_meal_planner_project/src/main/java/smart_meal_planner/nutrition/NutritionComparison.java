package smart_meal_planner.nutrition;

import smart_meal_planner.model.MealPlan;
import smart_meal_planner.model.NutrientEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import smart_meal_planner.model.MealDay;
import smart_meal_planner.model.UserNutritionalGoals;



public class NutritionComparison {
    
    public enum TrackedNutrient {
    CALORIES("Calories", UserNutritionalGoals::getDailyCaloriesGoal),
    PROTEIN("Protein", UserNutritionalGoals::getDailyProteinGoal),
    FAT("Fat", UserNutritionalGoals::getDailyFatGoal),
    CARBS("Carbohydrates", UserNutritionalGoals::getDailyCarbohydratesGoal),
    SAT_FAT("Saturated Fat", UserNutritionalGoals::getDailySaturatedFatGoal),
    SUGAR("Sugar", UserNutritionalGoals::getDailySugarGoal),
    CHOLESTEROL("Cholesterol", UserNutritionalGoals::getDailyCholesterolGoal),
    SODIUM("Sodium", UserNutritionalGoals::getDailySodiumGoal);

    private final String spoonName;
    private final java.util.function.Function<UserNutritionalGoals, Double> goalGetter;

    TrackedNutrient(String spoonName,
                    java.util.function.Function<UserNutritionalGoals, Double> goalGetter) {
        this.spoonName = spoonName;
        this.goalGetter = goalGetter;
    }

    public String getSpoonName() {
        return spoonName;
    }

    public double getGoal(UserNutritionalGoals goals) {
        return goalGetter.apply(goals);
    }

    public static TrackedNutrient fromName(String name) {
        for (TrackedNutrient t : values()) {
            if (t.spoonName.equalsIgnoreCase(name)) {
                return t;
            }
        }
        return null;
    }
    }


    //postiive value: less than goal's value for given nutrient in meal day. Negative value: exceeded the goal's value for given nutrient in meal day. 
    public double[][] compareNutrients(MealPlan mealPlan, UserNutritionalGoals goals)
    {

        //7 days, up to 8 possible nutritional goals 
        double[][] nutritionDifferences = new double[7][TrackedNutrient.values().length]; 
        int dayIndex = 0; 

        for (MealDay day : mealPlan.getDays()){
            Map<String, Double> combined = mergeNutrients(
                    day.getLunch().getNutrition().getNutrients(),
                    day.getDinner().getNutrition().getNutrients()
            );
           
            for (TrackedNutrient tn : TrackedNutrient.values())
            {
                double goal = tn.getGoal(goals);
                double combinedNutrientValue = combined.getOrDefault(tn.getSpoonName(), 0.0);
                double difference = goal - combinedNutrientValue; 

                nutritionDifferences[dayIndex][tn.ordinal()] = difference; 
            }
            dayIndex++; 
        }
        return nutritionDifferences; 
    }




    public Map<String, Double> mergeNutrients(List<NutrientEntity> list1, List<NutrientEntity> list2) {
        Map<String, Double> result = new HashMap<>();

        // Add first list
        for (NutrientEntity n : list1) {
            result.put(n.getName(), n.getAmount());
        }

        // Add second list — merge amounts
        for (NutrientEntity n : list2) {
            result.merge(n.getName(), n.getAmount(), Double::sum);
        }

        return result;
    }
}