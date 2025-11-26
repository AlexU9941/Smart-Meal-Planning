package smart_meal_planner.component;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class GroceryApiClient {

    private final RestTemplate rest;

    public GroceryApiClient(RestTemplateBuilder builder) {
        this.rest = builder.build();
    }

    public <T> T get(String url, String token, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<Void> req = new HttpEntity<>(headers);
        ResponseEntity<T> resp = rest.exchange(url, HttpMethod.GET, req, responseType);

        if (!resp.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Grocery API failed: " + resp.getStatusCode());
        }

        return resp.getBody();
    }

    public <T> T post(String url, Object body, String token, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> req = new HttpEntity<>(body, headers);
        ResponseEntity<T> resp = rest.postForEntity(url, req, responseType);

        if (!resp.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Grocery API failed: " + resp.getStatusCode());
        }

        return resp.getBody();
    }
}
