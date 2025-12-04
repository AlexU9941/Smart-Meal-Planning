package smart_meal_planner.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import smart_meal_planner.dto.MealPlanDTO;
import smart_meal_planner.dto.MealPlanResponseDTO;
import smart_meal_planner.model.MealDay;
import smart_meal_planner.model.MealPlan;
import smart_meal_planner.model.RecipeEntity;
import smart_meal_planner.repository.MealPlanRepository;
import smart_meal_planner.repository.RecipeRepository;

import java.util.ArrayList;
import java.util.List;

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

    // ================================================================
    //              CREATE MEAL PLAN FROM 21 RECIPE IDS
    // ================================================================
    public MealPlan createMealPlan(
            List<Long> breakfastIds,
            List<Long> lunchIds,
            List<Long> dinnerIds) {

        MealPlan plan = new MealPlan();
        List<MealDay> days = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            MealDay day = new MealDay();

            // Breakfast
            if (breakfastIds != null && i < breakfastIds.size()) {
                day.setBreakfast(recipeRepository.findById(breakfastIds.get(i)).orElse(null));
            }

            // Lunch
            if (lunchIds != null && i < lunchIds.size()) {
                day.setLunch(recipeRepository.findById(lunchIds.get(i)).orElse(null));
            }

            // Dinner
            if (dinnerIds != null && i < dinnerIds.size()) {
                day.setDinner(recipeRepository.findById(dinnerIds.get(i)).orElse(null));
            }

            day.setMealPlan(plan);
            day.setDay(getDayName(i));
            days.add(day);
        }

        plan.setDays(days);
        return mealPlanRepository.save(plan);
    }

    // ================================================================
    //                        GET MEAL PLAN
    // ================================================================
    public MealPlan getMealPlan(long planId) {
        return mealPlanRepository.findById(planId).orElse(null);
    }

    // ================================================================
    //                       DELETE MEAL PLAN
    // ================================================================
    public void deletePlan(long planId) {
        mealPlanRepository.deleteById(planId);
    }

    // ================================================================
    //               SAVE PLAN FROM FRONTEND MealPlanDTO
    // ================================================================
    public MealPlan saveMealPlan(MealPlanDTO dto) {

        List<Long> breakfastIds = new ArrayList<>();
        List<Long> lunchIds = new ArrayList<>();
        List<Long> dinnerIds = new ArrayList<>();

        for (MealPlanDTO.MealDayDTO day : dto.getDays()) {
            breakfastIds.add(day.getBreakfastId());
            lunchIds.add(day.getLunchId());
            dinnerIds.add(day.getDinnerId());
        }

        return createMealPlan(breakfastIds, lunchIds, dinnerIds);
    }

    // ================================================================
    //                       HELPER – day name
    // ================================================================
    private String getDayName(int i) {
        switch (i) {
            case 0: return "Sunday";
            case 1: return "Monday";
            case 2: return "Tuesday";
            case 3: return "Wednesday";
            case 4: return "Thursday";
            case 5: return "Friday";
            case 6: return "Saturday";
            default: return "Unknown";
        }
    }

    // ================================================================
    //               CONVERT MealPlan → MealPlanResponseDTO
    // ================================================================
    public MealPlanResponseDTO toDTO(MealPlan plan) {

        MealPlanResponseDTO dto = new MealPlanResponseDTO();
        dto.setPlanId(plan.getId());

        List<MealPlanResponseDTO.DayDTO> days = new ArrayList<>();

        for (MealDay day : plan.getDays()) {
            MealPlanResponseDTO.DayDTO d = new MealPlanResponseDTO.DayDTO();
            d.setDay(day.getDay());
            d.setBreakfast(simple(day.getBreakfast()));
            d.setLunch(simple(day.getLunch()));
            d.setDinner(simple(day.getDinner()));
            days.add(d);
        }

        dto.setDays(days);
        return dto;
    }

    private MealPlanResponseDTO.SimpleRecipeDTO simple(RecipeEntity recipe) {
        if (recipe == null) return null;

        MealPlanResponseDTO.SimpleRecipeDTO s = new MealPlanResponseDTO.SimpleRecipeDTO();
        s.setId(recipe.getId());
        s.setTitle(recipe.getTitle());
        s.setImage(recipe.getImage());
        s.setIngredients(recipe.getIngredients()); 
        return s;
    }
}
