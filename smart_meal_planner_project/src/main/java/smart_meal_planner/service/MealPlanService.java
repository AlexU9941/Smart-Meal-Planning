package smart_meal_planner.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import smart_meal_planner.dto.MealPlanDTO;
import smart_meal_planner.model.*;
import smart_meal_planner.nutrition.NutritionComparison;
import smart_meal_planner.repository.MealPlanRepository;
import smart_meal_planner.repository.RecipeRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MealPlanService {

    private final MealPlanRepository mealPlanRepository;
    private final RecipeRepository recipeRepository;

    public MealPlanService(MealPlanRepository mealPlanRepository, RecipeRepository recipeRepository) {
        this.mealPlanRepository = mealPlanRepository;
        this.recipeRepository = recipeRepository;
    }

    /** Save a meal plan from RecipeResult lists (used internally) */
    @Transactional
    public MealPlan saveMealPlan(List<RecipeEntity> lunchEntities, List<RecipeEntity> dinnerEntities) {
        MealPlan mealPlan = new MealPlan(lunchEntities, dinnerEntities);

        // Set back-references in MealDay
        for (MealDay day : mealPlan.getDays()) {
            day.setMealPlan(mealPlan);
        }

        return mealPlanRepository.save(mealPlan);
    }

    /** Save a meal plan from DTO containing recipe IDs */
    @Transactional
    public MealPlan saveMealPlan(MealPlanDTO dto) {
        // Fetch recipes by ID from DB
        List<RecipeEntity> lunchEntities = dto.getDays().stream()
                .map(day -> recipeRepository.findById(day.getLunchId().longValue())
                        .orElseThrow(() -> new RuntimeException("Lunch recipe not found: " + day.getLunchId())))
                .collect(Collectors.toList());

        List<RecipeEntity> dinnerEntities = dto.getDays().stream()
                .map(day -> recipeRepository.findById(day.getDinnerId().longValue())
                        .orElseThrow(() -> new RuntimeException("Dinner recipe not found: " + day.getDinnerId())))
                .collect(Collectors.toList());

        return saveMealPlan(lunchEntities, dinnerEntities);
    }

    /** Fetch a MealPlan by ID */
    @Transactional(readOnly = true)
    public MealPlan getMealPlan(Long id) {
        return mealPlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("MealPlan not found: " + id));
    }

    /** Compare meal plan nutrition against goals */
    @Transactional(readOnly = true)
    public double[][] compareNutrition(Long mealPlanId, UserNutritionalGoals goals) {
        MealPlan mealPlan = getMealPlan(mealPlanId);
        return new NutritionComparison().compareNutrients(mealPlan, goals);
    }
}
