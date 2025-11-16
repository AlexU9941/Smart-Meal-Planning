package smart_meal_planner.grocery;

import org.springframework.stereotype.Component;
import smart_meal_planner.dto.GroceryItem;
import smart_meal_planner.dto.OAuthResponse;

import java.util.ArrayList;
import java.util.List;

@Component
public class KrogerProvider implements GroceryStoreProvider {

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

}
