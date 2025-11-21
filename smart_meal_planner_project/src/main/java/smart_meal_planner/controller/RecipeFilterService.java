package smart_meal_planner.service;

import org.springframework.stereotype.Service;
import smart_meal_planner.recipe.RecipeResult;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecipeFilterService {

    public List<RecipeResult> filterRecipes(List<RecipeResult> recipes,
                                            String dietaryRestriction,
                                            List<String> ingredients,
                                            Integer maxPrepTime,
                                            String difficulty,
                                            String timeOfDay,
                                            Double maxCalories) {
        return recipes.stream()
                .filter(r -> dietaryRestriction == null || matchesDiet(r, dietaryRestriction))
                .filter(r -> ingredients == null || ingredients.isEmpty() || containsIngredients(r, ingredients))
                .filter(r -> maxPrepTime == null || r.getReadyInMinutes() <= maxPrepTime)
                .filter(r -> difficulty == null || matchesDifficulty(r, difficulty))
                .filter(r -> timeOfDay == null || matchesTimeOfDay(r, timeOfDay))
                .filter(r -> maxCalories == null || (r.getNutritionalInfo() != null &&
                        r.getNutritionalInfo().getCalories() <= maxCalories))
                .collect(Collectors.toList());
    }

    private boolean matchesDiet(RecipeResult recipe, String diet) {
        // Example: check dishTypes or add a dietaryRestriction field
        return recipe.getDishTypes() != null &&
               java.util.Arrays.asList(recipe.getDishTypes()).contains(diet);
    }

    private boolean containsIngredients(RecipeResult recipe, List<String> ingredients) {
        return recipe.getExtendedIngredients() != null &&
               recipe.getExtendedIngredients().stream()
                     .anyMatch(i -> ingredients.contains(i.getName().toLowerCase()));
    }

    private boolean matchesDifficulty(RecipeResult recipe, String difficulty) {
        // If difficulty is not stored, you can infer based on prep time
        if (difficulty.equalsIgnoreCase("Easy")) return recipe.getReadyInMinutes() <= 30;
        if (difficulty.equalsIgnoreCase("Medium")) return recipe.getReadyInMinutes() <= 60;
        return recipe.getReadyInMinutes() > 60;
    }

    private boolean matchesTimeOfDay(RecipeResult recipe, String timeOfDay) {
        return recipe.getDishTypes() != null &&
               java.util.Arrays.asList(recipe.getDishTypes()).contains(timeOfDay);
    }
}
