package smart_meal_planner.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;
import smart_meal_planner.model.MealPlan;
import smart_meal_planner.recipe.RecipeSearchResponse;

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
        //Join list into comma-separated String
        String ingredientsStr = String.join(",", ingredients);
        
        //Separate API calls for lunch/dinner, can add more different search criteria if needed
        Mono<RecipeSearchResponse> lunches = webClient.get()
            .uri(uriBuilder ->uriBuilder
                .path("/recipes/complexSearch")
                .queryParam("apiKey", apiKey)
                .queryParam("includeIngredients", ingredientsStr)
                .queryParam("maxPrice", maxPrice)
                .queryParam("addRecipeInformation", true)
                .queryParam("type", "lunch")
                .queryParam("number", 7) //# of meals
                .build())
            .retrieve()
            .bodyToMono(RecipeSearchResponse.class);


        Mono<RecipeSearchResponse> dinners = webClient.get()
            .uri(uriBuilder ->uriBuilder
                .path("/recipes/complexSearch")
                .queryParam("apiKey", apiKey)
                .queryParam("includeIngredients", ingredientsStr)
                .queryParam("maxPrice", maxPrice)
                .queryParam("addRecipeInformation", true)
                .queryParam("type", "dinner")
                .queryParam("number", 7) //# of meals
                .build())
            .retrieve()
            .bodyToMono(RecipeSearchResponse.class);

        RecipeSearchResponse lunch = lunches.block();
        RecipeSearchResponse dinner = dinners.block();   
        return new MealPlan(lunch.getResults(), dinner.getResults()); // Blocking for simplicity; consider using reactive patterns in production
    }
}
