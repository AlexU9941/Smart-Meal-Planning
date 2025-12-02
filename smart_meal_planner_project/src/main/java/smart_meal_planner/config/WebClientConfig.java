package smart_meal_planner.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    @Qualifier("krogerWebClient")
    public WebClient krogerWebClient() {
        return WebClient.builder()
                .baseUrl("https://api.kroger.com")
                .build();
    }
    @Bean
    @Qualifier("spoonacularWebClient")
    public WebClient spoonacularWebClient() {
        return WebClient.builder()
                .baseUrl("https://api.spoonacular.com")
                .build();
    }
}
