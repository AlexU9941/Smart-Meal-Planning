package smart_meal_planner.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import java.util.Set;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import reactor.core.publisher.Mono;
import smart_meal_planner.model.*;
import smart_meal_planner.dto.Ingredient;
import smart_meal_planner.recipe.RecipeResult;
import smart_meal_planner.recipe.RecipeSearchResponse;
import smart_meal_planner.repository.RecipeRepository;
import smart_meal_planner.repository.MealPlanRepository;

import java.util.ArrayList;
import java.util.Arrays;

@Service
public class RecipeService {

    private final WebClient webClient;
    private final RecipeRepository recipeRepository;
    private final MealPlanRepository mealPlanRepository;

    @Value("${spoonacular.api.key}")
    private String apiKey;

    public RecipeService(
        @Qualifier("spoonacularWebClient") WebClient spoonacularWebClient,
        RecipeRepository recipeRepository,
        MealPlanRepository mealPlanRepository
    ) {
        this.webClient = spoonacularWebClient;
        this.recipeRepository = recipeRepository;
        this.mealPlanRepository = mealPlanRepository;
    }

    /**
     * Finds recipes based on ingredients and max price,
     * then creates a persisted MealPlan (now includes breakfast).
     */
    public MealPlan findRecipeByString(List<String> ingredients, double maxPrice) {

        try {
            System.out.println("Querying API using: " + ingredients + " with budget " + maxPrice);

            // Default ingredients
            if (ingredients == null || ingredients.isEmpty()) {
                ingredients = Arrays.asList("eggs", "oats", "bread", "chicken", "beef", "vegetables");
            }

            List<RecipeResult> allResults = new ArrayList<>();
            int callsPerIngredient = Math.max(1, 100 / ingredients.size());

            // Query Spoonacular for each ingredient
            for (String ingredient : ingredients) {
                RecipeSearchResponse response =
                    fetchRecipesFromApi(ingredient, maxPrice, callsPerIngredient);

                if (response != null && response.getResults() != null) {
                    allResults.addAll(response.getResults());
                }
            }

            // Not enough results
            if (allResults == null || allResults.size() < 21) {
                System.out.println("Not enough results (" + allResults.size() + "). Returning empty plan.");
                return new MealPlan(); 
            }

            // Remove duplicates
            Map<Long, RecipeResult> uniqueResults = allResults.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(
                    RecipeResult::getId, 
                    r -> r, 
                    (r1, r2) -> r1
                ));

            // Score & sort recipes
            List<RecipeResult> scoredResults = scoreRecipes(
                new ArrayList<>(uniqueResults.values()),
                ingredients
            );

            // We need 21 meals: 7 breakfast + 7 lunch + 7 dinner
            int topMeals = Math.min(scoredResults.size(), 21);
            List<RecipeResult> topRecipes = scoredResults.subList(0, topMeals);

            return assignMealsAndPersist(topRecipes);

        } catch (WebClientResponseException e) {
            System.out.println("API error: " + e.getMessage());
            return new MealPlan();
        } catch (Exception e) {
            e.printStackTrace();
            return new MealPlan();
        }
    }

    /**
     * Calls Spoonacular API.
     */
    private RecipeSearchResponse fetchRecipesFromApi(String mainIngredient, double maxPrice, int callsPerIngredient) {

        return webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/recipes/complexSearch")
                .queryParam("query", mainIngredient)
                .queryParam("number", callsPerIngredient)
                .queryParam("addRecipeInformation", true)
                .queryParam("includeNutrition", true)
                .queryParam("maxPrice", maxPrice)
                .queryParam("apiKey", apiKey)
                .build()
            )
            .retrieve()
            .bodyToMono(RecipeSearchResponse.class)
            .block();
    }

    /**
     * Score recipes by ingredient matches.
     */
    private List<RecipeResult> scoreRecipes(List<RecipeResult> results, List<String> scoringIngredients) {

        for (RecipeResult recipe : results) {
            int score = 0;
            score += scoreByIngredient(recipe, scoringIngredients);
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

    /**
     * Build MealPlan with 7 breakfasts, 7 lunches, 7 dinners.
     */
    private MealPlan assignMealsAndPersist(List<RecipeResult> sorted) {

        MealPlan mealPlan = new MealPlan();
        List<MealDay> days = new ArrayList<>();

        // BREAKFAST (0–6)
        List<RecipeResult> breakfastResults = sorted.subList(0, 7);
        // LUNCH (7–13)
        List<RecipeResult> lunchResults = sorted.subList(7, 14);
        // DINNER (14–20)
        List<RecipeResult> dinnerResults = sorted.subList(14, 21);

        List<RecipeEntity> breakfastEntities = breakfastResults.stream()
            .map(this::saveOrGetRecipeEntity)
            .collect(Collectors.toList());

        List<RecipeEntity> lunchEntities = lunchResults.stream()
            .map(this::saveOrGetRecipeEntity)
            .collect(Collectors.toList());

        List<RecipeEntity> dinnerEntities = dinnerResults.stream()
            .map(this::saveOrGetRecipeEntity)
            .collect(Collectors.toList());

        // Create 7 meal days
        for (int i = 0; i < 7; i++) {
            MealDay day = new MealDay();

            day.setBreakfast(breakfastEntities.get(i));
            day.setLunch(lunchEntities.get(i));
            day.setDinner(dinnerEntities.get(i));

            day.setMealPlan(mealPlan);
            days.add(day);
        }

        mealPlan.setDays(days);

        return mealPlanRepository.save(mealPlan);
    }

    /**
     * Checks if the recipe already exists; if not, saves it.
     */
    private RecipeEntity saveOrGetRecipeEntity(RecipeResult r) {
        return recipeRepository.findById(r.getId())
            .orElseGet(() -> recipeRepository.save(RecipeEntity.fromRecipeResult(r)));
    }
}
