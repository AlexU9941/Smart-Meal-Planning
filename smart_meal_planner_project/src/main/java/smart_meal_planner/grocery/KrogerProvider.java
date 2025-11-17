package smart_meal_planner.grocery;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import smart_meal_planner.dto.GroceryItem;
import smart_meal_planner.dto.OAuthResponse;

import java.util.ArrayList;
import java.util.List;

@Component
public class KrogerProvider implements GroceryStoreProvider {

    @Value("${kroger.client-id}")
    private String clientId;

    @Value("${kroger.redirect-uri}")
    private String redirectUri;

    @Override
    public OAuthResponse exchangeAuthCode(String authCode) {
        // call Kroger API /token endpoint
        return null; // implement real token exchange
    }

    @Override
    public OAuthResponse refreshToken(String refreshToken) {
        // call Kroger API /token endpoint with grant_type=refresh_token
        return null;
    }

    @Override
    public List<GroceryItem> searchProducts(List<String> terms, String accessToken) {
        List<GroceryItem> results = new ArrayList<>();
        // call Kroger API for each term
        return results;
    }

    @Override
    public String buildCheckoutUrl(List<String> productIds, String accessToken) {
        return "https://www.kroger.com/checkout/start?items=" + String.join(",", productIds);
    }

    @Override
    public String getProviderName() {
        return "KROGER";
    }
    public String getAuthorizationUrl() {
        String redirectUrl = UriComponentsBuilder.fromHttpUrl("https://api.kroger.com/v1/connect/oauth2/authorize")
            .queryParam("client_id", clientId)
            .queryParam("response_type", "code")
            .queryParam("scope", "product.compact")
            .queryParam("redirect_uri", redirectUri)
            .build()
            .toUriString();
        return redirectUrl;
        }
}
