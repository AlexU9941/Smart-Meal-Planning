package smart_meal_planner.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import smart_meal_planner.dto.MealPlanDTO;
import smart_meal_planner.dto.MealPlanResponseDTO;
import smart_meal_planner.model.MealPlan;
import smart_meal_planner.service.MealPlanService;
import smart_meal_planner.service.RecipeService;
import smart_meal_planner.service.GenerateRequest;

@RestController
@RequestMapping("/meal-plans")
public class MealPlanController {

    private final MealPlanService mealPlanService;
    private final RecipeService recipeService;

    public MealPlanController(MealPlanService mealPlanService,
                              RecipeService recipeService) {
        this.mealPlanService = mealPlanService;
        this.recipeService = recipeService;
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

    // --------- FIXED RANDOM MEAL PLAN GENERATION -----------
    @PostMapping("/generate")
    public ResponseEntity<MealPlanResponseDTO> generateMealPlan(
            @RequestBody GenerateRequest request) {

        double budget = request.getBudget() != null ? request.getBudget() : 0.0;

        List<String> ingredients = (request.getIngredients() != null &&
                                    !request.getIngredients().isEmpty())
                ? request.getIngredients()
                : Arrays.asList("chicken", "beef", "vegetables");

        try {
            // Use your RecipeService logic
            MealPlan plan = recipeService.findRecipeByString(ingredients, budget);

            // Convert to DTO for frontend
            MealPlanResponseDTO response = mealPlanService.toDTO(plan);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(new MealPlanResponseDTO()); // empty response
        }
    }
}
