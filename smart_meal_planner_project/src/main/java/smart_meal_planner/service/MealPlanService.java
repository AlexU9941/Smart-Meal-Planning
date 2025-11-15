package smart_meal_planner.service;

package smart_meal_planner.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smart_meal_planner.model.*;
import smart_meal_planner.recipe.RecipeResult;
import smart_meal_planner.repository.MealPlanRepository;
import smart_meal_planner.repository.MealDayRepository;

import java.util.ArrayList;
import java.util.List;

import smart_meal_planner.model.MealPlanEntity;


@Service
public class MealPlanService {

    private final RecipeService recipeService;
    private final MealPlanRepository mealPlanRepo;
    private final MealDayRepository dayRepo;

    public MealPlanService(RecipeService recipeService,
                           MealPlanRepository mealPlanRepo,
                           MealDayRepository dayRepo) {
        this.recipeService = recipeService;
        this.mealPlanRepo = mealPlanRepo;
        this.dayRepo = dayRepo;
    }

    @Transactional
    public MealPlanEntity createMealPlan(List<RecipeResult> lunches, List<RecipeResult> dinners) {

        MealPlanEntity plan = new MealPlanEntity();
        mealPlanRepo.save(plan);

        int count = Math.min(Math.min(7, lunches.size()), dinners.size());

        List<MealDayEntity> days = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            RecipeEntity lunch = recipeService.saveFromApi(lunches.get(i));
            RecipeEntity dinner = recipeService.saveFromApi(dinners.get(i));

            MealDayEntity day = new MealDayEntity(lunch, dinner);
            day.setMealPlan(plan);
            dayRepo.save(day);

            days.add(day);
        }

        plan.setDays(days);
        return mealPlanRepo.save(plan);
    }

    public MealPlanEntity getMealPlan(Long id) {
        return mealPlanRepo.findById(id).orElse(null);
    }
}
