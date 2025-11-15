package smart_meal_planner.service;

import org.springframework.stereotype.Service;

@Service
public class NutritionService {

    @Autowired
    private MealPlanRepository mealPlanRepo;

    @Autowired
    private UserNutritionalGoalsRepository goalsRepo;

    public double[][] compareUserNutrition(Long mealPlanId, Long userId) {
        MealPlan mealPlan = mealPlanRepo.findById(mealPlanId).orElseThrow();
        UserNutritionalGoals goals = goalsRepo.findById(userId).orElseThrow();

        NutritionComparison comparison = new NutritionComparison();
        return comparison.compareNutrients(mealPlan, goals);
    }
}
