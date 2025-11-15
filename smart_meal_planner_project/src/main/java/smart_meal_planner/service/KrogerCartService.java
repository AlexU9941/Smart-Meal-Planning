package smart_meal_planner.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KrogerCartService {

    private KrogerAuthService authService;
    private RestTemplate restTemplate;

    private static final String CART_URL = "https://api.kroger.com/v1/cart";

    public Map createCart() {
        String token = authService.getAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);

        String emptyCartJson = "{\"items\":[]}";

        HttpEntity<String> request = new HttpEntity<>(emptyCartJson, headers);

        ResponseEntity<Map> response =
                restTemplate.postForEntity(CART_URL, request, Map.class);

        return response.getBody();
    }

    public Map addItemToCart(String cartId, String productId, int quantity) {
        String token = authService.getAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);

        Map<String, Object> item = Map.of(
                "itemId", productId,
                "quantity", quantity
        );

        Map<String, Object> payload = Map.of("items", List.of(item));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        String url = CART_URL + "/" + cartId + "/items";

        ResponseEntity<Map> response =
                restTemplate.postForEntity(url, request, Map.class);

        return response.getBody();
    }
}
