package smart_meal_planner.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import smart_meal_planner.dto.MealPlanDTO;
import smart_meal_planner.dto.MealPlanResponseDTO;
import smart_meal_planner.model.MealDay;
import smart_meal_planner.model.MealPlan;
import smart_meal_planner.model.RecipeEntity;
import smart_meal_planner.model.UserNutritionalGoals;
import smart_meal_planner.nutrition.NutritionComparison;
import smart_meal_planner.repository.MealPlanRepository;
import smart_meal_planner.repository.RecipeRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MealPlanService {

    private final MealPlanRepository mealPlanRepository;
    private final RecipeRepository recipeRepository;

    public MealPlanService(MealPlanRepository mealPlanRepository,
                           RecipeRepository recipeRepository) {

        this.mealPlanRepository = mealPlanRepository;
        this.recipeRepository = recipeRepository;
    }

    /** Save plan built manually */
    @Transactional
    public MealPlan saveMealPlan(List<RecipeEntity> breakfasts, List<RecipeEntity> lunches, List<RecipeEntity> dinners) {
        // MealPlan mealPlan = new MealPlan(breakfasts, lunches, dinners);

        // for (MealDay day : mealPlan.getDays()) {
        //     day.setMealPlan(mealPlan);
        // }

        // plan.setDays(days);
        // return mealPlanRepository.save(plan);

        MealPlan mealPlan = new MealPlan();
        List<MealDay> days = new ArrayList<>();

        String[] dayNames = { "Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday" };

        for (int i = 0; i < 7; i++) {
            MealDay day = new MealDay();
            day.setDay(dayNames[i]);
            day.setBreakfast(breakfasts.get(i));
            day.setLunch(lunches.get(i));
            day.setDinner(dinners.get(i));
            day.setMealPlan(mealPlan);

            days.add(day);
        }

        mealPlan.setDays(days);

        return mealPlanRepository.save(mealPlan);
    }

    /** Save plan built from DTO */
    @Transactional
    public MealPlan saveMealPlan(MealPlanDTO dto) {

        List<RecipeEntity> breakfasts = dto.getDays().stream()
            .map(day -> recipeRepository.findById(day.getBreakfastId().longValue())
            .orElseThrow(() -> new RuntimeException("Breakfast recipe not found: " + day.getBreakfastId())))
            .collect(Collectors.toList());

        List<RecipeEntity> lunches = dto.getDays().stream()
            .map(day -> recipeRepository.findById(day.getLunchId().longValue())
            .orElseThrow(() -> new RuntimeException("Lunch recipe not found: " + day.getLunchId())))
            .collect(Collectors.toList());

        List<RecipeEntity> dinners = dto.getDays().stream()
            .map(day -> recipeRepository.findById(day.getDinnerId().longValue())
            .orElseThrow(() -> new RuntimeException("Dinner recipe not found: " + day.getDinnerId())))
            .collect(Collectors.toList());

        return saveMealPlan(breakfasts, lunches, dinners);
    }

    public MealPlan getMealPlan(Long id) {
        return mealPlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("MealPlan not found: " + id));
    }

    public double[][] compareNutrition(Long mealPlanId, UserNutritionalGoals goals) {
        MealPlan mealPlan = getMealPlan(mealPlanId);
        return new NutritionComparison().compareNutrients(mealPlan, goals);
    }
}
