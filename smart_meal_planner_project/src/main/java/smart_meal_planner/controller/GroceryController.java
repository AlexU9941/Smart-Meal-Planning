package smart_meal_planner.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import smart_meal_planner.dto.GroceryItem;
import smart_meal_planner.dto.OAuthResponse;
import smart_meal_planner.grocery.GroceryStoreProvider;
import smart_meal_planner.service.GroceryIntegrationService;

import java.util.List;

@RestController
@RequestMapping("/api/grocery")
public class GroceryController {

    private final GroceryIntegrationService groceryService;
    private List<GroceryStoreProvider> groceryProviders;

    public GroceryController(GroceryIntegrationService groceryService, List<GroceryStoreProvider> groceryProviders) {
        this.groceryService = groceryService;
        this.groceryProviders = groceryProviders;
    }

    @PostMapping("/link")
    public String linkStore(@RequestParam String userId,
                            @RequestParam String provider,
                            @RequestBody OAuthResponse oauth) {
        groceryService.linkAccount(userId, provider, oauth);
        return "Account linked!";
    }

    @PostMapping("/search")
    public List<GroceryItem> searchIngredients(@RequestParam Long userId,
                                               @RequestBody List<String> ingredients) {
        return groceryService.lookupIngredients(userId, ingredients);
    }

    @PostMapping("/checkout")
    public String getCheckout(@RequestParam Long userId,
                              @RequestBody List<String> productIds) {
        return groceryService.getCheckoutRedirectUrl(userId, productIds);
    }

    @GetMapping("/auth/{providerName}")
    public ResponseEntity<String> authRedirect(@PathVariable("provider") String providerName) {

        // Find the provider by its unique name
        GroceryStoreProvider provider = groceryProviders.stream()
                .filter(p -> p.getProviderName().equalsIgnoreCase(providerName))
                .findFirst()
                .orElse(null);

        if (provider == null) {
            return ResponseEntity.badRequest().body("Unknown provider");
        }

        // Call the provider's method
        String url = provider.getAuthorizationUrl();
        return ResponseEntity.ok(url);
    }
}

