package smart_meal_planner.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class RecipeService {
    private final WebClient webClient; 

    @Value("${spoonacular.api.key}")
    private String apiKey;

    public RecipeService(WebClient spoonacularWebClient) {
        this.webClient = spoonacularWebClient;
    }

    //Will likely need to modify to accomadate future User criteria like nutriotional needs, dietary restrictions, etc.
    public String findRecipeByIngredients(List<String> ingredients, double maxPrice)
    {
        //Join list into comma-separated String
        String ingredientsStr = String.join(",", ingredients);

        Mono<String> response = webClient.get()
            .uri(uriBuilder ->uriBuilder
                .path("/recipes/complexSearch")
                .queryParam("includeIngredients", ingredientsStr)
                .queryParam("maxPrice", maxPrice)
                .queryParam("number", 14) //# of meals
                .build())
            .retrieve()
            .bodyToMono(String.class);

        return response.block(); // Blocking for simplicity; consider using reactive patterns in production
    }
}
