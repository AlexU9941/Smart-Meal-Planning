package smart_meal_planner.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KrogerCatalogService {

    private  KrogerAuthService authService;

    private  RestTemplate restTemplate;

    @Value("${kroger.location-id}")
    private String storeId;

    public Map search(String keyword) {
        String token = authService.getAccessToken();

        String url = String.format(
            "https://api.kroger.com/v1/products?filter.term=%s&filter.locationId=%s&filter.limit=5",
            UriUtils.encodeQueryParam(keyword, StandardCharsets.UTF_8),
            storeId
        );

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Map> response =
                restTemplate.exchange(url, HttpMethod.GET, request, Map.class);

        return response.getBody();
    }
}
