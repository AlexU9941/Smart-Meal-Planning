package smart_meal_planner.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    private final String SPOONACULAR_API_URL = "https://api.spoonacular.com";

    @Bean
    public WebClient spoonacularWebClient(){
        return WebClient.builder()
            .baseUrl(SPOONACULAR_API_URL)
            .build();
    }
}
