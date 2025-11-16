package smart_meal_planner.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import smart_meal_planner.controller.MealPlanDTO;
import smart_meal_planner.model.*;
import smart_meal_planner.nutrition.CaloricBreakdown;
import smart_meal_planner.nutrition.Nutrient;
import smart_meal_planner.nutrition.Nutrition;
import smart_meal_planner.nutrition.NutritionComparison;
import smart_meal_planner.nutrition.WeightPerServing;
import smart_meal_planner.recipe.Ingredient;
import smart_meal_planner.recipe.RecipeResult;
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


    @Transactional
    public MealPlan saveMealPlan(MealPlanDTO dto) {
        // Convert DTO to RecipeResult lists
        List<RecipeResult> lunchResults = dto.getDays().stream()
                .map(day -> getRecipeResultById(day.getLunchId()))
                .collect(Collectors.toList());

        List<RecipeResult> dinnerResults = dto.getDays().stream()
                .map(day -> getRecipeResultById(day.getDinnerId()))
                .collect(Collectors.toList());

        return createMealPlan(lunchResults, dinnerResults);
    }

  private RecipeResult getRecipeResultById(long id) {
    return recipeRepository.findById(id)
            .map(entity -> {
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
                r.setDishTypes(entity.getDishTypes().toArray(new String[0]));
                r.setScore(entity.getScore());

                // convert ingredients
                if (entity.getIngredients() != null) {
                    List<Ingredient> ingredients = entity.getIngredients()
                            .stream()
                            .map(ing -> {
                                Ingredient ingredient = new Ingredient();
                                ingredient.setName(ing.getName());
                                return ingredient;
                            })
                            .collect(Collectors.toList());
                    r.setExtendedIngredients(ingredients);
                }

                // convert nutrition
                if (entity.getNutrition() != null) {
                    r.setNutritionalInfo(toNutrition(entity.getNutrition()));
                }

                return r;
            })
            .orElseThrow(() -> new RuntimeException("Recipe not found: " + id));
}



    /**
     * Converts lists of RecipeResult for lunches and dinners into a MealPlan and saves it.
     * Cascades persist to MealDay, RecipeEntity, NutritionEntity, NutrientEntity, etc.
     */
    @Transactional
    public MealPlan createMealPlan(List<RecipeResult> lunchResults, List<RecipeResult> dinnerResults) {

        // Convert RecipeResults to RecipeEntities
        List<RecipeEntity> lunchEntities = lunchResults.stream()
                .map(this::saveOrGetRecipeEntity)
                .collect(Collectors.toList());

        List<RecipeEntity> dinnerEntities = dinnerResults.stream()
                .map(this::saveOrGetRecipeEntity)
                .collect(Collectors.toList());

        // Create MealPlan
        MealPlan mealPlan = new MealPlan(lunchEntities, dinnerEntities);

        // Set back-references in MealDay
        for (MealDay day : mealPlan.getDays()) {
            day.setMealPlan(mealPlan);
        }

        // Save MealPlan (cascade saves everything)
        return mealPlanRepository.save(mealPlan);
    }

    /**
     * Check if a RecipeEntity already exists by Spoonacular ID, otherwise convert and save it.
     */
    private RecipeEntity saveOrGetRecipeEntity(RecipeResult r) {
        return recipeRepository.findById(r.getId())
                .orElseGet(() -> {
                    RecipeEntity entity = RecipeEntity.fromRecipeResult(r);
                    return recipeRepository.save(entity);
                });
    }


   private RecipeResult toRecipeResult(RecipeEntity entity) {
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
    
    // Convert List<String> to String[]
    if (entity.getDishTypes() != null) {
        r.setDishTypes(entity.getDishTypes().toArray(new String[0]));
    }
    
    r.setScore(entity.getScore());
    
    // Convert ingredients
    if (entity.getIngredients() != null) {
        List<Ingredient> ingredients = entity.getIngredients()
            .stream()
            .map(ing -> {
                Ingredient ingredient = new Ingredient();
                ingredient.setName(ing.getName());
                // set other fields if needed
                return ingredient;
            })
            .collect(Collectors.toList()); // Java 8 compatible
        r.setExtendedIngredients(ingredients);
    }
    
    // Convert nutrition
    if (entity.getNutrition() != null) {
        r.setNutritionalInfo(toNutrition(entity.getNutrition()));
    }
    
    return r;
}

    // Helper method to convert NutritionEntity to Nutrition
    private Nutrition toNutrition(NutritionEntity entity) {
    Nutrition n = new Nutrition();
    
    if (entity.getNutrients() != null) {
        List<Nutrient> nutrients = entity.getNutrients()
            .stream()
            .map(ne -> {
                Nutrient nutr = new Nutrient();
                nutr.setName(ne.getName());
                nutr.setAmount(ne.getAmount());
                nutr.setUnit(ne.getUnit());
                nutr.setPercentOfDailyNeeds(ne.getPercentOfDailyNeeds());
                return nutr;
            })
            .collect(Collectors.toList());
        n.setNutrients(nutrients);
    }
    
    // Optional: map caloric breakdown
    if (entity.getPercentCarbs() != null || entity.getPercentProtein() != null || entity.getPercentFat() != null) {
        CaloricBreakdown cb = new CaloricBreakdown();
        cb.setPercentCarbs(entity.getPercentCarbs());
        cb.setPercentProtein(entity.getPercentProtein());
        cb.setPercentFat(entity.getPercentFat());
        n.setCaloricBreakdown(cb);
    }
    
    // Optional: map weight per serving
    if (entity.getAmount() != null && entity.getUnit() != null) {
        WeightPerServing wps = new WeightPerServing();
        wps.setAmount(entity.getAmount());
        wps.setUnit(entity.getUnit());
        n.setWeightPerServing(wps);
    }
    
    return n;
    }

    @Transactional(readOnly = true)
    public MealPlan getMealPlan(Long id) {
        return mealPlanRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("MealPlan not found: " + id));
    }

    @Transactional(readOnly = true)
    public double[][] compareNutrition(Long mealPlanId, UserNutritionalGoals goals) {
        MealPlan mealPlan = getMealPlan(mealPlanId);
        return new NutritionComparison().compareNutrients(mealPlan, goals);
    }


}
