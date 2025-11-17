package smart_meal_planner.grocery;

import smart_meal_planner.dto.GroceryItem;
import smart_meal_planner.dto.OAuthResponse;

import java.util.List;

public interface GroceryStoreProvider {

    // Exchange OAuth code for access token
    OAuthResponse exchangeAuthCode(String authCode);

    // Refresh access token
    OAuthResponse refreshToken(String refreshToken);

    // Search products by ingredient or term
    List<GroceryItem> searchProducts(List<String> terms, String accessToken);

    // Build checkout URL for a list of product IDs
    String buildCheckoutUrl(List<String> productIds, String accessToken);

    // Unique identifier for the store (KROGER, WALMART, etc.)
    String getProviderName();

    // Get authorization URL for OAuth flow
    String getAuthorizationUrl();
}
