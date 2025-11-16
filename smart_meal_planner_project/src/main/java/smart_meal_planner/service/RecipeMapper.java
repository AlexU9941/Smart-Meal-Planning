package smart_meal_planner.service;

import smart_meal_planner.model.*;
import smart_meal_planner.recipe.RecipeResult;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Maps between RecipeResult (API/DTO) and RecipeEntity (JPA)
 */
public class RecipeMapper {

    /** Converts a RecipeResult into a RecipeEntity for persistence */
    public static RecipeEntity toEntity(RecipeResult result) {
        RecipeEntity e = new RecipeEntity();
        e.setId(result.getId());
        e.setTitle(result.getTitle());
        e.setImage(result.getImage());
        e.setSourceUrl(result.getSourceUrl());
        e.setReadyInMinutes(result.getReadyInMinutes());
        e.setCookingMinutes(result.getCookingMinutes());
        e.setPreparationMinutes(result.getPreparationMinutes());
        e.setServings(result.getServings());
        e.setPricePerServing(result.getPricePerServing());
        if (result.getDishTypes() != null) {
            e.setDishTypes(Arrays.asList(result.getDishTypes()));
        }
        e.setScore(result.getScore());

        // Convert extended ingredients
        if (result.getExtendedIngredients() != null) {
            List<IngredientInput> ingredientInputs = IngredientInput.fromList(result.getExtendedIngredients(), e);
            e.setIngredients(ingredientInputs);
        }

        // Convert nutrition
        if (result.getNutritionalInfo() != null) {
            NutritionEntity nutritionEntity = NutritionEntity.fromNutrition(result.getNutritionalInfo());
            e.setNutrition(nutritionEntity);
        }

        return e;
    }

    /** Converts a RecipeEntity back to RecipeResult for API use or mapping */
    public static RecipeResult toResult(RecipeEntity entity) {
        RecipeResult r = new RecipeResult();
        r.setId(entity.getId());
        r.setTitle(entity.getTitle());
        r.setImage(entity.getImage());
        r.setSourceUrl(entity.getSourceUrl());
        r.setReadyInMinutes(entity.getReadyInMinutes());
        r.setCookingMinutes(entity.getCookingMinutes());
        r.setPreparationMinutes(entity.getPreparationMinutes());
        r.setServings(entity.getServings());
        r.setPricePerServing(entity.getPricePerServing());
        if (entity.getDishTypes() != null) {
            r.setDishTypes(entity.getDishTypes().toArray(new String[0]));
        }
        r.setScore(entity.getScore());

        // Convert ingredients
        if (entity.getIngredients() != null) {
            r.setExtendedIngredients(
                entity.getIngredients().stream()
                      .map(IngredientInput::toIngredient) // You need a helper method in IngredientInput
                      .collect(Collectors.toList())
            );
        }

        // Convert nutrition
        if (entity.getNutrition() != null) {
            r.setNutritionalInfo(entity.getNutrition().toNutrition()); // helper in NutritionEntity
        }

        return r;
    }
}
