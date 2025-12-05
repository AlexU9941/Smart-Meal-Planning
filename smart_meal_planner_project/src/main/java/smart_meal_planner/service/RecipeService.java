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





//WAS NOT WORKING FOR ME, replaced with earlier logic that works. 
// package smart_meal_planner.service;

// import java.util.ArrayList;
// import java.util.Comparator;
// import java.util.List;
// import java.util.Map;
// import java.util.Objects;
// import java.util.stream.Collectors;

// import org.springframework.beans.factory.annotation.Qualifier;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;
// import org.springframework.web.reactive.function.client.WebClient;
// import org.springframework.web.reactive.function.client.WebClientResponseException;

// import smart_meal_planner.dto.Ingredient;
// import smart_meal_planner.model.MealDay;
// import smart_meal_planner.model.MealPlan;
// import smart_meal_planner.model.RecipeEntity;
// import smart_meal_planner.dto.RandomRecipeResponse;
// import smart_meal_planner.recipe.RecipeResult;
// import smart_meal_planner.recipe.RecipeSearchResponse;
// import smart_meal_planner.repository.RecipeRepository;
// import smart_meal_planner.repository.MealPlanRepository;

// import java.util.ArrayList;
// import java.util.Arrays;

// @Service
// public class RecipeService {

//     private final WebClient webClient;
//     private final RecipeRepository recipeRepository;
//     private final MealPlanRepository mealPlanRepository;

//     @Value("${spoonacular.api.key}")
//     private String apiKey;

//     public RecipeService(
//         @Qualifier("spoonacularWebClient") WebClient spoonacularWebClient,
//         RecipeRepository recipeRepository,
//         MealPlanRepository mealPlanRepository
//     ) {
//         this.webClient = spoonacularWebClient;
//         this.recipeRepository = recipeRepository;
//         this.mealPlanRepository = mealPlanRepository;
//     }

//     /**
//      * Finds recipes based on ingredients and max price,
//      * then creates a persisted MealPlan (now includes breakfast).
//      */
//     public MealPlan findRecipeByString(List<String> ingredients, double maxPrice) {

//         boolean hasIngredients = ingredients != null && !ingredients.isEmpty();
//         boolean hasBudget = maxPrice != null && maxPrice > 0;

//         System.out.println("findRecipeByString -> ingredients=" + ingredients + ", maxPrice=" + maxPrice);
//         List<RecipeResult> all = new ArrayList<>();

//         try {
//             // CASE 1: NO INGREDIENTS + NO BUDGET → FULLY RANDOM
//             if (!hasIngredients && !hasBudget) {
//                 System.out.println("Case 1: no ingredients + no budget → fully random");
//                 all = fetchRandomRecipes(50); // fetch more than 21 to be safe
//             }
//             // CASE 2: INGREDIENTS ONLY → HONOR INGREDIENTS, IGNORE BUDGET
//             else if (hasIngredients && !hasBudget) {
//                 System.out.println("Case 2: ingredients only (no budget)");
//                 all = fetchRecipesForIngredients(ingredients, 20);
//             }
//             // CASE 3: BUDGET ONLY → RANDOM, THEN FILTER BY PRICE
//             else if (!hasIngredients && hasBudget) {
//                 System.out.println("Case 3: budget only (no ingredients)");
//                 List<RecipeResult> random = fetchRandomRecipes(50);
//                 all = random.stream()
//                         .filter(r -> r.getPricePerServing() > 0)
//                         .filter(r -> r.getPricePerServing() <= maxPrice)
//                         .collect(Collectors.toList());
//             }
//             // CASE 4: INGREDIENTS + BUDGET → HONOR BOTH
//             else {
//                 System.out.println("Case 4: ingredients + budget");
//                 List<RecipeResult> tmp = fetchRecipesForIngredients(ingredients, 20);
//                 all = tmp.stream()
//                         .filter(r -> r.getPricePerServing() > 0)
//                         .filter(r -> r.getPricePerServing() <= maxPrice)
//                         .collect(Collectors.toList());
//             }

//             if (all == null || all.isEmpty()) {
//                 System.out.println("No recipes found for the given criteria.");
//                 return new MealPlan(); // empty plan
//             }

//             // Remove duplicates by id
//             Map<Long, RecipeResult> uniqueMap = all.stream()
//                     .filter(Objects::nonNull)
//                     .collect(Collectors.toMap(
//                             RecipeResult::getId,
//                             r -> r,
//                             (r1, r2) -> r1 // keep first if duplicate
//                     ));

//             List<RecipeResult> uniqueList = new ArrayList<>(uniqueMap.values());

//             // If we have ingredients, score by ingredients; else keep original order
//             List<RecipeResult> ordered = hasIngredients
//                     ? scoreRecipes(uniqueList, ingredients)
//                     : uniqueList;

//             int days = 7;
//             int needed = days * 3; // breakfast + lunch + dinner
//             if (ordered.size() < needed) {
//                 System.out.println("Not enough recipes after filtering. Needed " + needed + ", got " + ordered.size());
//                 return new MealPlan();
//             }

//             // Take only what we need
//             List<RecipeResult> top = ordered.subList(0, needed);

//             return assignMealsAndPersist(top);

//         } catch (WebClientResponseException e) {
//             System.out.println("API error: " + e.getStatusCode() + " - " + e.getMessage());
//             return new MealPlan();
//         } catch (Exception e) {
//             e.printStackTrace();
//             return new MealPlan();
//         }
//     }






//         // try {
//         //     System.out.println("Querying API using: " + ingredients + " with budget " + maxPrice);

//         //     // Default ingredients
//         //     if (ingredients == null || ingredients.isEmpty()) {
//         //         ingredients = Arrays.asList("eggs", "oats", "bread", "chicken", "beef", "vegetables");
//         //     }



//         //     List<RecipeResult> allResults = new ArrayList<>();
//         //     int callsPerIngredient = Math.max(1, 10 / ingredients.size()); //reduced number of calls per ingredient for testing 

//         //     // Query Spoonacular for each ingredient
//         //     for (String ingredient : ingredients) {
//         //         RecipeSearchResponse response =
//         //             fetchRecipesFromApi(ingredient, maxPrice, callsPerIngredient);

//         //         if (response != null && response.getResults() != null) {
//         //             allResults.addAll(response.getResults());
//         //         }
//         //     }

//         //     // Not enough results
//         //     if (allResults == null || allResults.size() < 21) {
//         //         System.out.println("Not enough results (" + allResults.size() + "). Returning empty plan.");
//         //         return new MealPlan(); 
//         //     }

//         //     // Remove duplicates
//         //     Map<Long, RecipeResult> uniqueResults = allResults.stream()
//         //         .filter(Objects::nonNull)
//         //         .collect(Collectors.toMap(
//         //             RecipeResult::getId, 
//         //             r -> r, 
//         //             (r1, r2) -> r1
//         //         ));

//         //     // Score & sort recipes
//         //     // List<RecipeResult> scoredResults = scoreRecipes(
//         //     //     new ArrayList<>(uniqueResults.values()),
//         //     //     ingredients
//         //     // );

//         //     // // We need 21 meals: 7 breakfast + 7 lunch + 7 dinner
//         //     // int topMeals = Math.min(scoredResults.size(), 21);
//         //     // List<RecipeResult> topRecipes = scoredResults.subList(0, topMeals);


//         //     return assignMealsAndPersist(topRecipes);

//     //     } catch (WebClientResponseException e) {
//     //         System.out.println("API error: " + e.getMessage());
//     //         return new MealPlan();
//     //     } catch (Exception e) {
//     //         e.printStackTrace();
//     //         return new MealPlan();
//     //     }
//     // }

//     /**
//      * Calls Spoonacular API.
//      */
//     // private RecipeSearchResponse fetchRecipesFromApi(String mainIngredient, double maxPrice, int callsPerIngredient) {

//     //     return webClient.get()
//     //         .uri(uriBuilder -> uriBuilder
//     //             .path("/recipes/complexSearch")
//     //             .queryParam("query", mainIngredient)
//     //             .queryParam("number", callsPerIngredient)
//     //             .queryParam("addRecipeInformation", true)
//     //             .queryParam("includeNutrition", true)
//     //             .queryParam("maxPrice", maxPrice)
//     //             .queryParam("apiKey", apiKey)
//     //             .build()
//     //         )
//     //         .retrieve()
//     //         .bodyToMono(RecipeSearchResponse.class)
//     //         .block();
//     // }

//      /**
//      * Uses Spoonacular /recipes/random endpoint.
//      */
//     private List<RecipeResult> fetchRandomRecipes(int count) {
//         RandomRecipeResponse response = webClient.get()
//                 .uri(uriBuilder -> uriBuilder
//                         .path("/recipes/random")
//                         .queryParam("number", count)
//                         .queryParam("addRecipeInformation", true)
//                         .queryParam("includeNutrition", true)
//                         .queryParam("apiKey", apiKey)
//                         .build())
//                 .retrieve()
//                 .bodyToMono(RandomRecipeResponse.class)
//                 .block();

//         if (response == null || response.getRecipes() == null) {
//             return new ArrayList<>();
//         }
//         return response.getRecipes();
//     }



//     /**
//      * Score recipes by ingredient matches.
//      */
//     private List<RecipeResult> fetchRecipesForIngredients(List<String> ingredients, int numberPerIngredient) {
//         List<RecipeResult> combined = new ArrayList<>();

//         if (ingredients == null) return combined;

//         for (String ing : ingredients) {
//             if (ing == null || ing.trim().isEmpty()) {
//                 continue;
//             }

//             RecipeSearchResponse response = webClient.get()
//                     .uri(uriBuilder -> uriBuilder
//                             .path("/recipes/complexSearch")
//                             .queryParam("query", ing)
//                             .queryParam("number", numberPerIngredient)
//                             .queryParam("addRecipeInformation", true)
//                             .queryParam("includeNutrition", true)
//                             .queryParam("apiKey", apiKey)
//                             .build())
//                     .retrieve()
//                     .bodyToMono(RecipeSearchResponse.class)
//                     .block();

//             if (response != null && response.getResults() != null) {
//                 combined.addAll(response.getResults());
//             }
//         }

//         return combined;
//     }

//     // -----------------------------
//     // Scoring by ingredient
//     // -----------------------------
//     private List<RecipeResult> scoreRecipes(List<RecipeResult> results, List<String> scoringIngredients) {

//         for (RecipeResult recipe : results) {
//             int score = 0;
//             score += scoreByIngredient(recipe, scoringIngredients);
//             recipe.setScore(score);
//         }

//         return results.stream()
//             .sorted(Comparator.comparingInt(RecipeResult::getScore).reversed())
//             .distinct()
//             .collect(Collectors.toList());
//     }

//     private int scoreByIngredient(RecipeResult recipe, List<String> scoringIngredients) {

//         List<Ingredient> ingredients = recipe.getExtendedIngredients();
//         if (ingredients == null) return 0;

//         int score = 0;

//         for (Ingredient ingredient : ingredients) {
//             String name = ingredient.getName().toLowerCase();

//             if (scoringIngredients.stream().anyMatch(i -> name.contains(i.toLowerCase()))) {
//                 score += 5;
//             }
//         }

//         return score;
//     }

//     /**
//      * Build MealPlan with 7 breakfasts, 7 lunches, 7 dinners.
//      */
//     private MealPlan assignMealsAndPersist(List<RecipeResult> sorted) {

//         MealPlan mealPlan = new MealPlan();
//         List<MealDay> daysList = new ArrayList<>();

//         // BREAKFAST (0–6)
//         List<RecipeResult> breakfastResults = sorted.subList(0, 7);
//         // LUNCH (7–13)
//         List<RecipeResult> lunchResults = sorted.subList(7, 14);
//         // DINNER (14–20)
//         List<RecipeResult> dinnerResults = sorted.subList(14, 21);

//         List<RecipeEntity> breakfastEntities = breakfastResults.stream()
//             .map(this::saveOrGetRecipeEntity)
//             .collect(Collectors.toList());

//         List<RecipeEntity> lunchEntities = lunchResults.stream()
//             .map(this::saveOrGetRecipeEntity)
//             .collect(Collectors.toList());

//         List<RecipeEntity> dinnerEntities = dinnerResults.stream()
//                 .map(this::saveOrGetRecipeEntity)
//                 .collect(Collectors.toList());

//         // Create 7 meal days
//         for (int i = 0; i < 7; i++) {
//             MealDay day = new MealDay();

//             day.setBreakfast(breakfastEntities.get(i));
//             day.setLunch(lunchEntities.get(i));
//             day.setDinner(dinnerEntities.get(i));

//             day.setMealPlan(mealPlan);
//             daysList.add(day);
//         }

//         mealPlan.setDays(daysList);

//         return mealPlanRepository.save(mealPlan);
//     }

//     /**
//      * Checks if the recipe already exists; if not, saves it.
//      */
//     private RecipeEntity saveOrGetRecipeEntity(RecipeResult r) {
//         return recipeRepository.findById(r.getId())
//             .orElseGet(() -> recipeRepository.save(RecipeEntity.fromRecipeResult(r)));
//     }
// }
