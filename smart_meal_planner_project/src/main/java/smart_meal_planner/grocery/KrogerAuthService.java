package smart_meal_planner.grocery;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.json.JSONObject;

import smart_meal_planner.grocery.KrogerConfig;

import java.util.Base64;

@Service
public class KrogerAuthService {

    private final KrogerConfig config;
    private final RestTemplate restTemplate = new RestTemplate();

    public KrogerAuthService(KrogerConfig config) {
        this.config = config;
    }

    public String getAccessToken() {
        String auth = config.clientId + ":" + config.clientSecret;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + encodedAuth);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String body = "grant_type=client_credentials&scope=" + config.scope;

        HttpEntity<String> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response =
                restTemplate.exchange(config.tokenUrl, HttpMethod.POST, request, String.class);

        JSONObject json = new JSONObject(response.getBody());
        return json.getString("access_token");
    }
}
