package smart_meal_planner.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import smart_meal_planner.dto.Ingredient;
import smart_meal_planner.model.MealDay;
import smart_meal_planner.model.MealPlan;
import smart_meal_planner.model.RecipeEntity;
import smart_meal_planner.dto.RandomRecipeResponse;
import smart_meal_planner.recipe.RecipeResult;
import smart_meal_planner.recipe.RecipeSearchResponse;
import smart_meal_planner.repository.MealPlanRepository;
import smart_meal_planner.repository.RecipeRepository;

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
            MealPlanRepository mealPlanRepository) {
        this.webClient = spoonacularWebClient;
        this.recipeRepository = recipeRepository;
        this.mealPlanRepository = mealPlanRepository;
    }

    /**
     * Main entry: ingredients + optional budget.
     * Implements 4 cases:
     * 1) no ingredients + no budget → fully random
     * 2) ingredients only → honor ingredients, ignore price
     * 3) budget only → random then filter by price
     * 4) ingredients + budget → filter by both
     */
    @Transactional
    public MealPlan findRecipeByString(List<String> ingredients, Double maxPrice) {

        boolean hasIngredients = ingredients != null && !ingredients.isEmpty();
        boolean hasBudget = maxPrice != null && maxPrice > 0;

        System.out.println("findRecipeByString -> ingredients=" + ingredients + ", maxPrice=" + maxPrice);
        List<RecipeResult> all = new ArrayList<>();

        try {
            // CASE 1: NO INGREDIENTS + NO BUDGET → FULLY RANDOM
            if (!hasIngredients && !hasBudget) {
                System.out.println("Case 1: no ingredients + no budget → fully random");
                all = fetchRandomRecipes(50); // fetch more than 21 to be safe
            }
            // CASE 2: INGREDIENTS ONLY → HONOR INGREDIENTS, IGNORE BUDGET
            else if (hasIngredients && !hasBudget) {
                System.out.println("Case 2: ingredients only (no budget)");
                all = fetchRecipesForIngredients(ingredients, 20);
            }
            // CASE 3: BUDGET ONLY → RANDOM, THEN FILTER BY PRICE
            else if (!hasIngredients && hasBudget) {
                System.out.println("Case 3: budget only (no ingredients)");
                List<RecipeResult> random = fetchRandomRecipes(50);
                all = random.stream()
                        .filter(r -> r.getPricePerServing() > 0)
                        .filter(r -> r.getPricePerServing() <= maxPrice)
                        .collect(Collectors.toList());
            }
            // CASE 4: INGREDIENTS + BUDGET → HONOR BOTH
            else {
                System.out.println("Case 4: ingredients + budget");
                List<RecipeResult> tmp = fetchRecipesForIngredients(ingredients, 20);
                all = tmp.stream()
                        .filter(r -> r.getPricePerServing() > 0)
                        .filter(r -> r.getPricePerServing() <= maxPrice)
                        .collect(Collectors.toList());
            }

            if (all == null || all.isEmpty()) {
                System.out.println("No recipes found for the given criteria.");
                return new MealPlan(); // empty plan
            }

            // Remove duplicates by id
            Map<Long, RecipeResult> uniqueMap = all.stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toMap(
                            RecipeResult::getId,
                            r -> r,
                            (r1, r2) -> r1 // keep first if duplicate
                    ));

            List<RecipeResult> uniqueList = new ArrayList<>(uniqueMap.values());

            // If we have ingredients, score by ingredients; else keep original order
            List<RecipeResult> ordered = hasIngredients
                    ? scoreRecipes(uniqueList, ingredients)
                    : uniqueList;

            int days = 7;
            int needed = days * 3; // breakfast + lunch + dinner
            if (ordered.size() < needed) {
                System.out.println("Not enough recipes after filtering. Needed " + needed + ", got " + ordered.size());
                return new MealPlan();
            }

            // Take only what we need
            List<RecipeResult> top = ordered.subList(0, needed);

            return assignMealsAndPersist(top);

        } catch (WebClientResponseException e) {
            System.out.println("API error: " + e.getStatusCode() + " - " + e.getMessage());
            return new MealPlan();
        } catch (Exception e) {
            e.printStackTrace();
            return new MealPlan();
        }
    }

    /**
     * Overload: ingredients as DTOs → just map to names and reuse above.
     */
    @Transactional
    public MealPlan findRecipeByIngredients(List<Ingredient> ingredients, Double maxPrice) {
        List<String> names = null;

        if (ingredients != null && !ingredients.isEmpty()) {
            names = ingredients.stream()
                    .filter(Objects::nonNull)
                    .map(Ingredient::getName)
                    .collect(Collectors.toList());
        }

        return findRecipeByString(names, maxPrice);
    }

    // -----------------------------
    // API call helpers
    // -----------------------------

    /**
     * Uses Spoonacular /recipes/random endpoint.
     */
    private List<RecipeResult> fetchRandomRecipes(int count) {
        RandomRecipeResponse response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/recipes/random")
                        .queryParam("number", count)
                        .queryParam("apiKey", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(RandomRecipeResponse.class)
                .block();

        if (response == null || response.getRecipes() == null) {
            return new ArrayList<>();
        }
        return response.getRecipes();
    }

    /**
     * Uses Spoonacular /recipes/complexSearch for each ingredient.
     */
    private List<RecipeResult> fetchRecipesForIngredients(List<String> ingredients, int numberPerIngredient) {
        List<RecipeResult> combined = new ArrayList<>();

        if (ingredients == null) return combined;

        for (String ing : ingredients) {
            if (ing == null || ing.trim().isEmpty()) {
                continue;
            }

            RecipeSearchResponse response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/recipes/complexSearch")
                            .queryParam("query", ing)
                            .queryParam("number", numberPerIngredient)
                            .queryParam("addRecipeInformation", true)
                            .queryParam("includeNutrition", true)
                            .queryParam("apiKey", apiKey)
                            .build())
                    .retrieve()
                    .bodyToMono(RecipeSearchResponse.class)
                    .block();

            if (response != null && response.getResults() != null) {
                combined.addAll(response.getResults());
            }
        }

        return combined;
    }

    // -----------------------------
    // Scoring by ingredient
    // -----------------------------
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
        if (ingredients == null || scoringIngredients == null || scoringIngredients.isEmpty()) {
            return 0;
        }

        int score = 0;
        for (Ingredient ingredient : ingredients) {
            String name = ingredient.getName().toLowerCase();
            if (scoringIngredients.stream().anyMatch(i -> name.contains(i.toLowerCase()))) {
                score += 5;
            }
        }
        return score;
    }

    // -----------------------------
    // Persist MealPlan with breakfast + lunch + dinner
    // -----------------------------
    @Transactional
    private MealPlan assignMealsAndPersist(List<RecipeResult> sorted) {
        int days = 7;
        int needed = days * 3;

        if (sorted.size() < needed) {
            System.out.println("assignMealsAndPersist: not enough recipes, expected " + needed + " got " + sorted.size());
            return new MealPlan();
        }

        List<RecipeResult> breakfastResults = sorted.subList(0, days);
        List<RecipeResult> lunchResults = sorted.subList(days, 2 * days);
        List<RecipeResult> dinnerResults = sorted.subList(2 * days, 3 * days);

        MealPlan mealPlan = new MealPlan();
        List<MealDay> daysList = new ArrayList<>();

        List<RecipeEntity> breakfastEntities = breakfastResults.stream()
                .map(this::saveOrGetRecipeEntity)
                .collect(Collectors.toList());

        List<RecipeEntity> lunchEntities = lunchResults.stream()
                .map(this::saveOrGetRecipeEntity)
                .collect(Collectors.toList());

        List<RecipeEntity> dinnerEntities = dinnerResults.stream()
                .map(this::saveOrGetRecipeEntity)
                .collect(Collectors.toList());

        for (int i = 0; i < days; i++) {
            MealDay day = new MealDay();
            day.setBreakfast(breakfastEntities.get(i));
            day.setLunch(lunchEntities.get(i));
            day.setDinner(dinnerEntities.get(i));
            day.setMealPlan(mealPlan);
            daysList.add(day);
        }

        mealPlan.setDays(daysList);
        return mealPlanRepository.save(mealPlan);
    }

    /**
     * Checks if RecipeEntity exists by Spoonacular ID, otherwise converts and saves it.
     */
    private RecipeEntity saveOrGetRecipeEntity(RecipeResult r) {
        return recipeRepository.findById(r.getId())
                .orElseGet(() -> recipeRepository.save(RecipeEntity.fromRecipeResult(r)));
    }
}
