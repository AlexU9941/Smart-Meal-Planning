package smart_meal_planner.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import smart_meal_planner.dto.MealPlanDTO;
import smart_meal_planner.dto.MealPlanResponseDTO;
import smart_meal_planner.model.MealDay;
import smart_meal_planner.model.MealPlan;
import smart_meal_planner.model.RecipeEntity;
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
    public ResponseEntity<MealPlanResponseDTO> generateMealPlan(
            @RequestBody GenerateRequest request) {

        Long userId = request.getUserId();
        System.out.println(">>> FRONTEND SENT USER ID = " + userId);

        double budget = request.getBudget() != null ? request.getBudget() : 0.0;

        List<String> ingredients = (request.getIngredients() != null &&
                !request.getIngredients().isEmpty())
                ? request.getIngredients()
                : Arrays.asList("chicken", "beef", "vegetables");

        try {
            MealPlan plan = recipeService.findRecipeByString(ingredients, budget);

            boolean emptyPlan = (plan == null ||
                    plan.getDays() == null ||
                    plan.getDays().isEmpty());

            if (emptyPlan) {
                if (userId != null) {
                    MealPlan existing = mealPlanRepository.findTopByUserIdOrderByIdDesc(userId);
                    if (existing != null &&
                        existing.getDays() != null &&
                        !existing.getDays().isEmpty()) {

                        return ResponseEntity.ok(mealPlanService.toDTO(existing));
                    }
                }

                return ResponseEntity.ok(new MealPlanResponseDTO());
            }

            // FIX: Assign correct user ID
            if (userId != null) {
                plan.setUserId(userId);
            }

            // Assign back-reference to each MealDay
            for (MealDay d : plan.getDays()) {
                d.setMealPlan(plan);
            }

            plan = mealPlanRepository.save(plan);

            return ResponseEntity.ok(mealPlanService.toDTO(plan));

        } catch (Exception e) {
            e.printStackTrace();

            if (userId != null) {
                MealPlan existing = mealPlanRepository.findTopByUserIdOrderByIdDesc(userId);
                if (existing != null &&
                    existing.getDays() != null &&
                    !existing.getDays().isEmpty()) {

                    return ResponseEntity.ok(mealPlanService.toDTO(existing));
                }
            }

            return ResponseEntity.ok(new MealPlanResponseDTO());
        }
    }

    @PostMapping("/alternative")
    public MealPlanResponseDTO.SimpleRecipeDTO alternativeMeal(
            @RequestParam("dayId") Long dayId,
            @RequestParam("mealType") String mealType) {

        RecipeEntity updated = recipeService.replaceMealInDay(dayId, mealType);
        return mealPlanService.toSimpleRecipeDTO(updated);
    }
}
