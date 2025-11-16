package smart_meal_planner.service;

import org.springframework.stereotype.Service;
import smart_meal_planner.grocery.GroceryStoreProvider;
import smart_meal_planner.model.GroceryAccount;
import smart_meal_planner.repository.GroceryAccountRepository;
import smart_meal_planner.dto.GroceryItem;
import smart_meal_planner.dto.OAuthResponse;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GroceryIntegrationService {

    private final Map<String, GroceryStoreProvider> providers;
    private final GroceryAccountRepository accountRepository;

    public GroceryIntegrationService(List<GroceryStoreProvider> providerList,
                                     GroceryAccountRepository accountRepository) {
        this.providers = new HashMap<>();
        for (GroceryStoreProvider p : providerList) {
            providers.put(p.getProviderName(), p);
        }
        this.accountRepository = accountRepository;
    }

    /**
     * Links a grocery store account for a user.
     */
    public void linkAccount(String userId, String providerName, OAuthResponse oauth) {
        GroceryStoreProvider provider = providers.get(providerName);
        if (provider == null) {
            throw new IllegalArgumentException("Provider not supported: " + providerName);
        }

        GroceryAccount acc = new GroceryAccount();
        acc.setUserId(userId);
        acc.setProvider(providerName);
        acc.setAccessToken(oauth.getAccessToken());
        acc.setRefreshToken(oauth.getRefreshToken());
        acc.setExpiresAt(oauth.getExpiresAt());

        accountRepository.save(acc);
    }

    /**
     * Searches for a list of ingredients for the user's linked grocery account.
     */
    public List<GroceryItem> lookupIngredients(String userId, List<String> ingredients) {
        GroceryAccount acc = getAccount(userId);
        GroceryStoreProvider provider = providers.get(acc.getProvider());

        if (provider == null) {
            throw new RuntimeException("Provider not found: " + acc.getProvider());
        }

        if (tokenExpired(acc)) {
            OAuthResponse oauth = provider.refreshToken(acc.getRefreshToken());
            acc.setAccessToken(oauth.getAccessToken());
            acc.setRefreshToken(oauth.getRefreshToken());
            acc.setExpiresAt(oauth.getExpiresAt());
            accountRepository.save(acc);
        }

        return provider.searchProducts(ingredients, acc.getAccessToken());
    }

    /**
     * Builds a checkout redirect URL for the selected items.
     */
    public String getCheckoutRedirectUrl(String userId, List<String> productIds) {
        GroceryAccount acc = getAccount(userId);
        GroceryStoreProvider provider = providers.get(acc.getProvider());

        if (provider == null) {
            throw new RuntimeException("Provider not found: " + acc.getProvider());
        }

        return provider.buildCheckoutUrl(productIds, acc.getAccessToken());
    }

    // ----------------- Helper Methods -----------------

    private GroceryAccount getAccount(String userId) {
        return accountRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Grocery account not linked for user: " + userId));
    }

    private boolean tokenExpired(GroceryAccount acc) {
        return acc.getExpiresAt() == null || Instant.now().isAfter(acc.getExpiresAt());
    }
}
