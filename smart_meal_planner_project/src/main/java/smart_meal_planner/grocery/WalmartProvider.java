package smart_meal_planner.grocery;

import org.springframework.stereotype.Component;
import smart_meal_planner.dto.GroceryItem;
import smart_meal_planner.dto.OAuthResponse;

import java.util.ArrayList;
import java.util.List;

@Component
public class WalmartProvider implements GroceryStoreProvider {

    @Override
    public OAuthResponse exchangeAuthCode(String authCode) {
        // implement Walmart OAuth token exchange
        return null;
    }

    @Override
    public OAuthResponse refreshToken(String refreshToken) {
        // implement Walmart token refresh
        return null;
    }

    @Override
    public List<GroceryItem> searchProducts(List<String> terms, String accessToken) {
        // call Walmart API
        return new ArrayList<>();
    }

    @Override
    public String buildCheckoutUrl(List<String> productIds, String accessToken) {
        return "https://www.walmart.com/cart?items=" + String.join(",", productIds);
    }

    @Override
    public String getProviderName() {
        return "WALMART";
    }
}
