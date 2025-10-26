package smart_meal_planner.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    // @Value("${spoonacular.api.key}")
    // private String apiKey; 
    //private final String SPOONACULAR_API_URL = "https://api.spoonacular.com/recipes/complexSearch?apiKey=" + apiKey;

    @Bean
    public WebClient spoonacularWebClient(){
        //String SPOONACULAR_API_URL = "https://api.spoonacular.com/recipes/complexSearch?apiKey=" + apiKey;
        return WebClient.builder()
            .baseUrl("https://api.spoonacular.com")
            .build();
    }
}
