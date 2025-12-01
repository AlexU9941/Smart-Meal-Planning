package smart_meal_planner.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;
import smart_meal_planner.dto.Ingredient;
import smart_meal_planner.model.*;
import smart_meal_planner.recipe.RecipeResult;
import smart_meal_planner.recipe.RecipeSearchResponse;
import smart_meal_planner.repository.RecipeRepository;
import smart_meal_planner.repository.MealPlanRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecipeService {

    private final WebClient webClient;
    private final RecipeRepository recipeRepository;
    private final MealPlanRepository mealPlanRepository;

    @Value("${spoonacular.api.key}")
    private String apiKey;

    public RecipeService(@Qualifier("spoonacularWebClient") WebClient spoonacularWebClient,
                         RecipeRepository recipeRepository,
                         MealPlanRepository mealPlanRepository) {
        this.webClient = spoonacularWebClient;
        this.recipeRepository = recipeRepository;
        this.mealPlanRepository = mealPlanRepository;
    }

    /**
     * Recommends recipes similar to favorites based on shared ingredients.
     */
    public List<RecipeResult> findSimilarRecipes(List<Long> favoriteIds) {
        if (favoriteIds == null || favoriteIds.isEmpty()) return Collections.emptyList();

        List<RecipeEntity> favoriteRecipes = recipeRepository.findAllById(favoriteIds);

        Set<String> favoriteIngredients = favoriteRecipes.stream()
                .flatMap(r -> r.getIngredients().stream())
                .map(i -> i.getName().toLowerCase())
                .collect(Collectors.toSet());

        List<RecipeEntity> allRecipes = recipeRepository.findAll();

        return allRecipes.stream()
                .filter(r -> !favoriteIds.contains(r.getId()))
                .map(RecipeEntity::toRecipeResult)
                .filter(r -> r.getExtendedIngredients().stream()
                        .anyMatch(i -> favoriteIngredients.contains(i.getName().toLowerCase())))
                .collect(Collectors.toList());
    }

    /**
     * Finds recipes based on a list of ingredient names and max price.
     */
    public MealPlan findRecipeByString(List<String> ingredients, double maxPrice) {
        if (ingredients.isEmpty()) {
            throw new IllegalArgumentException("Ingredient list cannot be empty");
        }

        String mainIngredient = ingredients.get(0);
        List<String> scoringIngredients = ingredients.subList(1, ingredients.size());

        RecipeSearchResponse response = fetchRecipesFromApi(mainIngredient, maxPrice);
        if (response == null || response.getResults() == null || response.getResults().isEmpty()) {
            throw new RuntimeException("No recipes returned from Spoonacular API");
        }

        List<RecipeResult> scoredResults = scoreRecipes(response.getResults(), scoringIngredients);
        return assignMealsAndPersist(scoredResults);
    }

    public MealPlan findRecipeByIngredients(List<Ingredient> ingredients, double maxPrice) {
        List<String> ingredientNames;
        if (ingredients.isEmpty()) {
            ingredientNames = Arrays.asList("chicken", "beef", "vegetables");
        } else {
            ingredientNames = ingredients.stream()
                    .filter(Objects::nonNull)
                    .map(Ingredient::getName)
                    .collect(Collectors.toList());
        }

        double price = maxPrice > 0 ? maxPrice : 100;
        return findRecipeByString(ingredientNames, price);
    }

    private RecipeSearchResponse fetchRecipesFromApi(String mainIngredient, double maxPrice) {
        int requestCount = 50;

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/recipes/complexSearch")
                        .queryParam("query", mainIngredient)
                        .queryParam("number", requestCount)
                        .queryParam("addRecipeInformation", true)
                        .queryParam("includeNutrition", true)
                        .queryParam("maxPrice", maxPrice)
                        .queryParam("apiKey", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(RecipeSearchResponse.class)
                .block();
    }

    private List<RecipeResult> scoreRecipes(List<RecipeResult> results, List<String> scoringIngredients) {
        for (RecipeResult recipe : results) {
            int score = scoreByIngredient(recipe, scoringIngredients);
            recipe.setScore(score);
        }

        return results.stream()
                .sorted(Comparator.comparingInt(RecipeResult::getScore).reversed())
                .distinct()
                .collect(Collectors.toList());
    }

    private int scoreByIngredient(RecipeResult recipe, List<String> scoringIngredients) {
        List<Ingredient> ingredients = recipe.getExtendedIngredients();
        if (ingredients == null) return 0;

        int score = 0;
        for (Ingredient ingredient : ingredients) {
            String name = ingredient.getName().toLowerCase();
            if (scoringIngredients.stream().anyMatch(i -> name.contains(i.toLowerCase()))) {
                score += 5;
            }
        }
        return score;
    }

    private MealPlan assignMealsAndPersist(List<RecipeResult> sorted) {
        List<RecipeResult> top14 = sorted.stream().limit(14).collect(Collectors.toList());
        List<RecipeResult> lunchResults = top14.subList(0, 7);
        List<RecipeResult> dinnerResults = top14.subList(7, 14);

        List<RecipeEntity> lunchEntities = lunchResults.stream()
                .map(this::saveOrGetRecipeEntity)
                .collect(Collectors.toList());

        List<RecipeEntity> dinnerEntities = dinnerResults.stream()
                .map(this::saveOrGetRecipeEntity)
                .collect(Collectors.toList());

        MealPlan mealPlan = new MealPlan(lunchEntities, dinnerEntities);

        for (MealDay day : mealPlan.getDays()) {
            day.setMealPlan(mealPlan);
        }

        return mealPlanRepository.save(mealPlan);
    }

    private RecipeEntity saveOrGetRecipeEntity(RecipeResult r) {
        return recipeRepository.findById(r.getId())
                .orElseGet(() -> recipeRepository.save(RecipeEntity.fromRecipeResult(r)));
    }
}
