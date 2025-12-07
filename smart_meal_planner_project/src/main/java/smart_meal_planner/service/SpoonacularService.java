package smart_meal_planner.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class SpoonacularService {

    private final WebClient spoonacularWebClient;

    @Value("${spoonacular.api.key}")
    private String apiKey;

    public SpoonacularService(
        @Qualifier("spoonacularWebClient") WebClient spoonacularWebClient
    ) {
        this.spoonacularWebClient = spoonacularWebClient;
    }

    public Map<String, Object> getRandomRecipe() {
        return spoonacularWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/recipes/random")
                        .queryParam("number", 1)
                        .queryParam("apiKey", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }
}
