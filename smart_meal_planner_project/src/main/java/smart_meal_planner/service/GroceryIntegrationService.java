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


    public List<GroceryItem> lookupIngredients(Long userId, List<String> ingredients) {
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

    public String getCheckoutRedirectUrl(Long userId, List<String> productIds) {
        GroceryAccount acc = getAccount(userId);
        GroceryStoreProvider provider = providers.get(acc.getProvider());

        if (provider == null) {
            throw new RuntimeException("Provider not found: " + acc.getProvider());
        }

        return provider.buildCheckoutUrl(productIds, acc.getAccessToken());
    }

    private GroceryAccount getAccount(Long userId) {
        return accountRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Grocery account not linked for user: " + userId));
    }

    private boolean tokenExpired(GroceryAccount acc) {
        return acc.getExpiresAt() == null || Instant.now().isAfter(acc.getExpiresAt());
    }
}
