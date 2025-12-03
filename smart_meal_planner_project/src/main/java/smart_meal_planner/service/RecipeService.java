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
import smart_meal_planner.repository.UserNutritionalGoalsRepository;
import smart_meal_planner.repository.MealPlanRepository;

import java.util.ArrayList;
import java.util.Arrays;


import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecipeService {

    private final WebClient webClient;
    private final RecipeRepository recipeRepository;
    private final MealPlanRepository mealPlanRepository;

    //private final UserNutritionalGoalsRepository goalsRepository;

    @Value("${spoonacular.api.key}")
    private String apiKey;

    public RecipeService(@Qualifier("spoonacularWebClient") WebClient spoonacularWebClient, RecipeRepository recipeRepository, MealPlanRepository mealPlanRepository/*, UserNutritionalGoalsRepository goalsRepository*/) {
        this.webClient = spoonacularWebClient;
        this.recipeRepository = recipeRepository;
        this.mealPlanRepository = mealPlanRepository;
        //this.goalsRepository = goalsRepository;
    }

    /**
     * Finds recipes based on ingredients and max price, then creates a persisted MealPlan.
     */
     public MealPlan findRecipeByString(List<String> ingredients, double maxPrice) {
        // if (ingredients == null|| ingredients.isEmpty()) {
        //     throw new IllegalArgumentException("Ingredient list cannot be empty");
        // }
        try{
        System.out.println("Querying API using: " + ingredients + " with budget " + maxPrice);


        if (ingredients == null || ingredients.isEmpty()) 
            {
              ingredients = Arrays.asList("chicken", "beef", "vegetables");
            }
            

        // String mainIngredient = ingredients.get(0);
        // List<String> scoringIngredients = ingredients.subList(0, ingredients.size());
       
        List<RecipeResult> allResults = new ArrayList<>();
        int callsPerIngredient = Math.max(1, 100 / ingredients.size());

        for (String ingredient : ingredients) {
            RecipeSearchResponse response = fetchRecipesFromApi(ingredient, maxPrice, callsPerIngredient);
            if (response != null && response.getResults() != null) {
                allResults.addAll(response.getResults());
            }
        }

        if (allResults == null || allResults.size() < 14) {
            // No recipes found — do NOT create a meal plan
            System.out.println("No recipes found for the given ingredients and budget.");
            return new MealPlan(); // empty plan
        }

        // Remove duplicates by recipe ID
        Map<Long, RecipeResult> uniqueResults = allResults.stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toMap(RecipeResult::getId, r -> r, (r1, r2) -> r1));

        List<RecipeResult> scoredResults = scoreRecipes(new ArrayList<>(uniqueResults.values()), ingredients);

        //Take top 14
        int topMeals = Math.min(scoredResults.size(), 14);
        List<RecipeResult> topRecipes = scoredResults.subList(0, topMeals);

        return assignMealsAndPersist(topRecipes);
    } catch (WebClientResponseException e) {
        // Handle specific HTTP errors
        if (e.getStatusCode().value() == 402) {
            System.out.println("API limit reached or payment required: " + e.getMessage());
        } else {
            System.out.println("API error: " + e.getStatusCode() + " - " + e.getMessage());
        }
        return new MealPlan(); // return empty plan instead of throwing
    } catch (Exception e) {
        e.printStackTrace();
        return new MealPlan(); // fallback for all other exceptions
    }
       // RecipeSearchResponse response = fetchRecipesFromApi(mainIngredient, maxPrice);
    //    RecipeSearchResponse response = fetchRecipesFromApi(ingredients, maxPrice);
    //     if (response == null || response.getResults() == null || response.getResults().isEmpty()) {
    //         throw new RuntimeException("No recipes returned from Spoonacular API");
    //     }

        //TRIED FILTERING BUT SEEMS TO REDUCE RESULTS TOO MUCH
        //filter by price per serving
        // List<RecipeResult> filteredResults = response.getResults().stream()
        //     .filter(r -> r.getPricePerServing() > 0  && r.getPricePerServing() <= maxPrice)
        //     .collect(Collectors.toList());

        //filter by ingredients, must include at least one scoring ingredient
        // filteredResults = filteredResults.stream()
        // .filter(r -> r.getExtendedIngredients().stream()
        // .anyMatch(i -> scoringIngredients.contains(i.getName().toLowerCase())))
        // .collect(Collectors.toList());

        //filter by ingredients, must use ALL selected ingredients (or substrings w/ ingredient name)
    //    filteredResults = filteredResults.stream()
    //     .filter(r -> {
    //         // Collect ingredient names from this recipe

    //         // Skip recipes with no ingredient list
    //         if (r.getExtendedIngredients() == null) {
    //             return false;
    //         }

    //         Set<String> names = r.getExtendedIngredients().stream()
    //                 .filter(Objects::nonNull)
    //                 .map(i -> i.getName().toLowerCase())
    //                 .collect(Collectors.toSet());

    //         // Every scoring ingredient must appear at least once (substring match)
    //        long count = scoringIngredients.stream()
    //         .filter(ing -> names.stream().anyMatch(n -> n.contains(ing)))
    //         .count();

    //         return count >= names.size()/2; // at least 1/2 of ingredients must match
    //     })
    //     .collect(Collectors.toList());

      //  List<RecipeResult> scoredResults = scoreRecipes(filteredResults, scoringIngredients);

        // // Score and sort recipes
        // List<RecipeResult> scoredResults = scoreRecipes(response.getResults(), scoringIngredients);

        // // Assign meals and persist
        // return assignMealsAndPersist(scoredResults);
    }


        public MealPlan findRecipeByIngredients(List<Ingredient> ingredients, double maxPrice) {
            List<String> ingredientNames; 

            if (ingredients == null || ingredients.isEmpty()) 
            {
              ingredientNames = Arrays.asList("chicken", "beef", "vegetables");
            }
            
            else 
            {
            ingredientNames = ingredients.stream()
                    .filter(Objects::nonNull)
                    .map(Ingredient::getName)
                    .collect(Collectors.toList());
            }

            double price = maxPrice > 0 ? maxPrice : 100; // default max price
    
            return findRecipeByString(ingredientNames, price);
        }


    /**
     * Calls Spoonacular API synchronously to get recipes.
     */
   // private RecipeSearchResponse fetchRecipesFromApi(String mainIngredient, double maxPrice) {
    private RecipeSearchResponse fetchRecipesFromApi(String mainIngredient, double maxPrice, int callsPerIngredient) {
       // int requestCount = 100; // fetch more results to ensure variety

      
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/recipes/complexSearch")
                        .queryParam("query", mainIngredient)
                        .queryParam("number", callsPerIngredient)
                        .queryParam("addRecipeInformation", true)
                        .queryParam("includeNutrition", true)
                        .queryParam("maxPrice", maxPrice)
                        .queryParam("apiKey", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(RecipeSearchResponse.class)
                .block();
                
    }



    /**
     * Scores recipes based on presence of scoring ingredients.
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


    private int scoreByIngredient(RecipeResult recipe, List<String> scoringIngredients)
    {
        List<Ingredient> ingredients = recipe.getExtendedIngredients();
        if (ingredients == null)
        {
            return 0; 
        }

        int score = 0; 

        for (Ingredient ingredient : ingredients) {
                    String name = ingredient.getName().toLowerCase();
                    if (scoringIngredients.stream().anyMatch(i -> name.contains(i.toLowerCase()))) {
                        score+= 5;
                    }
                }
        
        return score; 
    }

    //to score by nutrition with current setup would need to compare meal days, maybe do later. 
    // private int scoreByNutrition(RecipeResult recipe, UserNutritionalGoals goals)
    // {
    //     if (recipe.getNutritionalInfo() != null)
    //     {

    //     }
    // }


    /**
     * Converts top scored RecipeResults to RecipeEntity and creates MealPlan.
     */
    private MealPlan assignMealsAndPersist(List<RecipeResult> sorted) {
        // List<RecipeResult> top14 = sorted.stream()
        //         .limit(14)
        //         .collect(Collectors.toList());

        MealPlan mealPlan = new MealPlan();
        List<MealDay> days = new ArrayList<>();

        List<RecipeResult> lunchResults = sorted.subList(0, 7);
        List<RecipeResult> dinnerResults = sorted.subList(7, 14);

        // Convert RecipeResult -> RecipeEntity and persist
         List<RecipeEntity> lunchEntities = lunchResults.stream()
            .map(this::saveOrGetRecipeEntity)
            .collect(Collectors.toList());

        List<RecipeEntity> dinnerEntities = dinnerResults.stream()
            .map(this::saveOrGetRecipeEntity)
            .collect(Collectors.toList());

        // Build MealDays (handle missing meals)
        for (int i = 0; i < 7; i++) {
            MealDay day = new MealDay();
            day.setLunch(i < lunchEntities.size() ? lunchEntities.get(i) : null);
            day.setDinner(i < dinnerEntities.size() ? dinnerEntities.get(i) : null);
            day.setMealPlan(mealPlan);
            days.add(day);
        }

        mealPlan.setDays(days);

        // Persist meal plan
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



//CLASS BEFORE PERSISTENCE, keeping in case issues with above
// @Service
// public class RecipeService {
//     private final WebClient webClient; 

//     @Value("${spoonacular.api.key}")
//     private String apiKey;

//     public RecipeService(WebClient spoonacularWebClient) {
//         this.webClient = spoonacularWebClient;
//     }

//     //Will likely need to modify to accomadate future User criteria like nutriotional needs, dietary restrictions, etc.
//     public MealPlan findRecipeByIngredients(List<String> ingredients, double maxPrice)
//     {
        
//         String mainIngredient = ingredients.get(0); 
//         List<String> scoringIngredients = ingredients.subList(1, ingredients.size());
//         //Join list into comma-separated String
//         //String scoringIngredients = String.join(",", ingredients.subList(1, ingredients.size()));


//         int requestCount = 50; //get more than needed to avoid shortages/duplicates

//         //TOO FEW RESULTS when trying to filter with all ingredients. Instead take the first ingredient, 
//         //score 50 results based on presence of other ingredients at take the top 14. 
//         Mono<RecipeSearchResponse> meals = webClient.get()
//             .uri(uriBuilder ->uriBuilder
//                 .path("/recipes/complexSearch")
//                 .queryParam("query", mainIngredient)
//                 .queryParam("apiKey", apiKey)
//                // .queryParam("includeIngredients", includeIngredients)
//                 .queryParam("maxPrice", maxPrice)
//                 .queryParam("addRecipeInformation", true)
//                 //.queryParam("type", "lunch")
//                 .queryParam("number", requestCount) //# of meals
//                 .queryParam("includeNutrition", true)
//                 .build())
//             .retrieve()
//             .bodyToMono(RecipeSearchResponse.class);

//         //Separate API calls for lunch/dinner, can add more different search criteria if needed
//         // Mono<RecipeSearchResponse> dinners = webClient.get()
//         //     .uri(uriBuilder ->uriBuilder
//         //         .path("/recipes/complexSearch")
//         //         .queryParam("query", mainIngredient)
//         //         .queryParam("apiKey", apiKey)
//         //         .queryParam("includeIngredients", includeIngredients)
//         //         .queryParam("maxPrice", maxPrice)
//         //         .queryParam("addRecipeInformation", true)
//         //         //.queryParam("type", "dinner")
//         //         .queryParam("number", requestCount) //# of meals
//         //         .build())
//         //     .retrieve()
//         //     .bodyToMono(RecipeSearchResponse.class);

//         RecipeSearchResponse response = meals.block();
//         List<RecipeResult> scoredResults = scoreRecipes(response.getResults(), scoringIngredients); 

        
//         return assignMeals(scoredResults);
//     }



//     private List<RecipeResult> scoreRecipes(List<RecipeResult> results, List<String> scoringIngredients)
//     {
//        for (RecipeResult recipe : results)
//        {
//             int score = 0; 
//             if (recipe.getExtendedIngredients() != null)
//             {
//                 for (Ingredient ingredient : recipe.getExtendedIngredients())
//                 {
//                    String name = ingredient.getName().toLowerCase();

//                    if (scoringIngredients.stream()
//                         .anyMatch(i -> name.contains(i.toLowerCase())))
//                         {
//                             score++; 
//                         }
//                 }
//             }
//             recipe.setScore(score);
//         }

//         return results.stream()
//             .sorted(Comparator.comparingInt(RecipeResult::getScore).reversed())
//             .distinct()
//             .collect(Collectors.toList());
//     }


//     private MealPlan assignMeals(List<RecipeResult> sorted)
//     {
//         List<RecipeResult> top14 = sorted.stream()
//             .limit(14)
//             .collect(Collectors.toList());

//         List<RecipeResult> lunches = top14.subList(0, 7);
//         List<RecipeResult> dinners = top14.subList(7, 14);

//         return new MealPlan(lunches, dinners);
//     }












      // private MealPlan makeBalancedMealPlan(List<RecipeResult> lunchResults, List<RecipeResult> dinnerResults)
    // {
    //     //Make lunch list unique 
    //     List<RecipeResult> lunch = lunchResults.stream()
    //         .distinct()
    //         .limit(7)
    //         .collect(Collectors.toList());

    //     //obtain lunch IDs to ensure dinners are unique
    //     Set<Integer> lunchIds = lunch.stream()
    //         .map(RecipeResult::getId)
    //         .collect(Collectors.toSet());

    //     //Make dinner list unique and not overlapping with lunch
    //     List<RecipeResult> dinner = dinnerResults.stream()
    //         .filter(d -> !lunchIds.contains(d.getId()))
    //         .distinct()
    //         .limit(7)
    //         .collect(Collectors.toList());

    //     System.out.println("Lunch total returned: " + lunch.size());
    //     System.out.println("Lunch unique before limit: " + lunch.stream().distinct().count());

    //     System.out.println("Dinner total returned: " + dinner.size());
    //     System.out.println("Dinner unique before limit: " + dinner.stream().distinct().count());

    //     return new MealPlan(lunch, dinner);
        
    // }
//}

