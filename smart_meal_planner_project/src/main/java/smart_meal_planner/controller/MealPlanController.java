package smart_meal_planner.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.web.bind.annotation.*;

import smart_meal_planner.dto.MealPlanDTO;
import smart_meal_planner.model.MealDay;
import smart_meal_planner.model.MealPlan;
import smart_meal_planner.model.UserNutritionalGoals;
import smart_meal_planner.repository.MealPlanRepository;
import smart_meal_planner.service.GenerateRequest;
import smart_meal_planner.service.MealPlanService;
import smart_meal_planner.service.RecipeService;

@RestController
@RequestMapping("/meal-plans")
public class MealPlanController {

    private final MealPlanService mealPlanService;
    private final RecipeService recipeService;
    private final MealPlanRepository mealPlanRepository;

    public MealPlanController(
            MealPlanService mealPlanService,
            RecipeService recipeService,
            MealPlanRepository mealPlanRepository) {

        this.mealPlanService = mealPlanService;
        this.recipeService = recipeService;
        this.mealPlanRepository = mealPlanRepository;
    }

    // Save a meal plan from frontend-provided IDs
    @PostMapping
    public MealPlan createMealPlan(@RequestBody MealPlanDTO dto) {
        return mealPlanService.saveMealPlan(dto);
    }

    @GetMapping("/{id}")
    public MealPlan getMealPlan(@PathVariable Long id) {
        return mealPlanService.getMealPlan(id);
    }

    @PostMapping("/{id}/nutrition-diff")
    public double[][] getNutritionDiff(@PathVariable Long id,
                                       @RequestBody UserNutritionalGoals goals) {
        return mealPlanService.compareNutrition(id, goals);
    }

    @PostMapping("/generate")
    public MealPlan generateMealPlan(@RequestBody GenerateRequest request) {

        double budget = request.getBudget() != null ? request.getBudget() : 100.0;

        List<String> ingredients =
                (request.getIngredients() != null && !request.getIngredients().isEmpty())
                        ? request.getIngredients()
                        : Arrays.asList("eggs", "oats", "fruit", "bread", "chicken", "vegetables");

        try {
            // Generate and persist a meal plan (breakfast/lunch/dinner) from Spoonacular
            MealPlan plan = recipeService.findRecipeByString(ingredients, budget);

            if (plan == null) {
                return new MealPlan();
            }

            // Attach current user ID (so the plan is owned by this user)
            if (request.getUserId() != null) {
                plan.setUserId(request.getUserId());
            }

            // Ensure each MealDay has the correct back-reference to this plan
            if (plan.getDays() != null) {
                for (MealDay day : plan.getDays()) {
                    day.setMealPlan(plan);
                }
            }

            // Save or update the plan with userId included
            return mealPlanRepository.save(plan);

        } catch (Exception e) {
            e.printStackTrace();
            return new MealPlan();
        }
    }
}
