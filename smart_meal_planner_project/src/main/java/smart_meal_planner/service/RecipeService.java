package smart_meal_planner.service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;
import smart_meal_planner.model.MealPlan;
import smart_meal_planner.recipe.RecipeSearchResponse;
import smart_meal_planner.recipe.Ingredient;
import smart_meal_planner.recipe.RecipeResult;


@Service
public class RecipeService {
    private final WebClient webClient; 

    @Value("${spoonacular.api.key}")
    private String apiKey;

    public RecipeService(WebClient spoonacularWebClient) {
        this.webClient = spoonacularWebClient;
    }

    //Will likely need to modify to accomadate future User criteria like nutriotional needs, dietary restrictions, etc.
    public MealPlan findRecipeByIngredients(List<String> ingredients, double maxPrice)
    {
        
        String mainIngredient = ingredients.get(0); 
        List<String> scoringIngredients = ingredients.subList(1, ingredients.size());
        //Join list into comma-separated String
        //String scoringIngredients = String.join(",", ingredients.subList(1, ingredients.size()));


        int requestCount = 50; //get more than needed to avoid shortages/duplicates

        //TOO FEW RESULTS when trying to filter with all ingredients. Instead take the first ingredient, 
        //score 50 results based on presence of other ingredients at take the top 14. 
        Mono<RecipeSearchResponse> meals = webClient.get()
            .uri(uriBuilder ->uriBuilder
                .path("/recipes/complexSearch")
                .queryParam("query", mainIngredient)
                .queryParam("apiKey", apiKey)
               // .queryParam("includeIngredients", includeIngredients)
                .queryParam("maxPrice", maxPrice)
                .queryParam("addRecipeInformation", true)
                //.queryParam("type", "lunch")
                .queryParam("number", requestCount) //# of meals
                .build())
            .retrieve()
            .bodyToMono(RecipeSearchResponse.class);

        //Separate API calls for lunch/dinner, can add more different search criteria if needed
        // Mono<RecipeSearchResponse> dinners = webClient.get()
        //     .uri(uriBuilder ->uriBuilder
        //         .path("/recipes/complexSearch")
        //         .queryParam("query", mainIngredient)
        //         .queryParam("apiKey", apiKey)
        //         .queryParam("includeIngredients", includeIngredients)
        //         .queryParam("maxPrice", maxPrice)
        //         .queryParam("addRecipeInformation", true)
        //         //.queryParam("type", "dinner")
        //         .queryParam("number", requestCount) //# of meals
        //         .build())
        //     .retrieve()
        //     .bodyToMono(RecipeSearchResponse.class);

        RecipeSearchResponse response = meals.block();
        List<RecipeResult> scoredResults = scoreRecipes(response.getResults(), scoringIngredients); 

        
        return assignMeals(scoredResults);
    }



    private List<RecipeResult> scoreRecipes(List<RecipeResult> results, List<String> scoringIngredients)
    {
       for (RecipeResult recipe : results)
       {
            int score = 0; 
            if (recipe.getExtendedIngredients() != null)
            {
                for (Ingredient ingredient : recipe.getExtendedIngredients())
                {
                   String name = ingredient.getName().toLowerCase();

                   if (scoringIngredients.stream()
                        .anyMatch(i -> name.contains(i.toLowerCase())))
                        {
                            score++; 
                        }
                }
            }
            recipe.setScore(score);
        }

        return results.stream()
            .sorted(Comparator.comparingInt(RecipeResult::getScore).reversed())
            .distinct()
            .collect(Collectors.toList());
    }


    private MealPlan assignMeals(List<RecipeResult> sorted)
    {
        List<RecipeResult> top14 = sorted.stream()
            .limit(14)
            .collect(Collectors.toList());

        List<RecipeResult> lunches = top14.subList(0, 7);
        List<RecipeResult> dinners = top14.subList(7, 14);

        return new MealPlan(lunches, dinners);
    }












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
}

