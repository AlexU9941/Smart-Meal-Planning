package smart_meal_planner.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import smart_meal_planner.dto.MealPlanDTO;
import smart_meal_planner.dto.MealPlanResponseDTO;
import smart_meal_planner.model.MealDay;
import smart_meal_planner.model.MealPlan;
import smart_meal_planner.model.RecipeEntity;
import smart_meal_planner.repository.MealDayRepository;
import smart_meal_planner.repository.MealPlanRepository;

@Service
public class MealPlanService {

    private final MealPlanRepository mealPlanRepository;
    private final MealDayRepository mealDayRepository;

    public MealPlanService(MealPlanRepository mealPlanRepository,
                           MealDayRepository mealDayRepository) {
        this.mealPlanRepository = mealPlanRepository;
        this.mealDayRepository = mealDayRepository;
    }

    public MealPlan saveMealPlan(MealPlanDTO dto) {
        MealPlan plan = new MealPlan();
        plan.setUserId(dto.getUserId());
        return mealPlanRepository.save(plan);
    }

    public MealPlan getMealPlan(Long id) {
        return mealPlanRepository.findById(id).orElse(null);
    }

    // ---------------- CONVERT TO DTO ----------------

    public MealPlanResponseDTO toDTO(MealPlan plan) {
        MealPlanResponseDTO dto = new MealPlanResponseDTO();
        dto.setPlanId(plan.getId());

        List<MealPlanResponseDTO.DayDTO> days = new ArrayList<>();

        for (MealDay d : plan.getDays()) {
            MealPlanResponseDTO.DayDTO dayDTO = new MealPlanResponseDTO.DayDTO();
            dayDTO.setDayId(d.getId());
            dayDTO.setDay(d.getDay());

            dayDTO.setBreakfast(toSimpleRecipeDTO(d.getBreakfast()));
            dayDTO.setLunch(toSimpleRecipeDTO(d.getLunch()));
            dayDTO.setDinner(toSimpleRecipeDTO(d.getDinner()));

            days.add(dayDTO);
        }

        dto.setDays(days);
        return dto;
    }

    // ---------------- SIMPLE RECIPE DTO BUILDER ----------------

    public MealPlanResponseDTO.SimpleRecipeDTO toSimpleRecipeDTO(RecipeEntity entity) {
        if (entity == null) return null;

        return new MealPlanResponseDTO.SimpleRecipeDTO(
                entity.getId(),
                entity.getTitle(),
                entity.getImage(),
                entity.getSourceUrl()
        );
    }

    public double[][] compareNutrition(Long id, Object goals) {
        // Your existing logic remains unchanged
        return new double[0][0];
    }
}
