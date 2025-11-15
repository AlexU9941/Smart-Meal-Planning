package smart_meal_planner.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import smart_meal_planner.service.KrogerCartService;
import smart_meal_planner.service.KrogerCatalogService;
import smart_meal_planner.service.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/shopping")
@RequiredArgsConstructor
public class ShoppingController {

    private KrogerCatalogService catalogService;
    private KrogerCartService cartService;

    @PostMapping("/price")
    public ResponseEntity<?> getPrices(@RequestBody List<String> items) {
        List<Map> results = new ArrayList<>();

        for (String item : items) {
            Map searchResult = catalogService.search(item);
            results.add(Map.of(
                    "query", item,
                    "results", searchResult.get("data")
            ));
        }

        return ResponseEntity.ok(results);
    }

    @PostMapping("/addToCart")
    public ResponseEntity<?> addToCart(@RequestBody Map<String, Object> body) {
        List<Map<String, Object>> items = (List<Map<String, Object>>) body.get("items");

        Map<String, Object> cart = cartService.createCart();
        String cartId = (String) cart.get("cartId");

        for (Map<String, Object> item : items) {
            String productId = (String) item.get("productId");
            int quantity = (int) item.get("quantity");
            cartService.addItemToCart(cartId, productId, quantity);
        }

        return ResponseEntity.ok(Map.of(
                "cartId", cartId,
                "message", "Items added successfully"
        ));
    }
}
