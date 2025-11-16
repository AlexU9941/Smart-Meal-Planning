package smart_meal_planner.controller;

import org.springframework.web.bind.annotation.*;
import smart_meal_planner.dto.GroceryItem;
import smart_meal_planner.dto.OAuthResponse;
import smart_meal_planner.service.GroceryIntegrationService;

import java.util.List;

@RestController
@RequestMapping("/api/grocery")
public class GroceryController {

    private final GroceryIntegrationService groceryService;

    public GroceryController(GroceryIntegrationService groceryService) {
        this.groceryService = groceryService;
    }

    @PostMapping("/link")
    public String linkStore(@RequestParam String userId,
                            @RequestParam String provider,
                            @RequestBody OAuthResponse oauth) {
        groceryService.linkAccount(userId, provider, oauth);
        return "Account linked!";
    }

    @PostMapping("/search")
    public List<GroceryItem> searchIngredients(@RequestParam String userId,
                                               @RequestBody List<String> ingredients) {
        return groceryService.lookupIngredients(userId, ingredients);
    }

    @PostMapping("/checkout")
    public String getCheckout(@RequestParam String userId,
                              @RequestBody List<String> productIds) {
        return groceryService.getCheckoutRedirectUrl(userId, productIds);
    }
}

