package smart_meal_planner.service;

import org.apache.tomcat.util.http.parser.MediaType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import io.netty.handler.codec.http.HttpHeaders;

@Service
public class KrogerAuthService {

    @Value("${kroger.client-id}")
    private String clientId;

    @Value("${kroger.client-secret}")
    private String clientSecret;

    private static final String TOKEN_URL = "https://api.kroger.com/v1/connect/oauth2/token";

    private final RestTemplate restTemplate = new RestTemplate();

    public String getAccessToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(clientId, clientSecret);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "client_credentials");
        form.add("scope", "product.compact");

        HttpEntity<MultiValueMap<String, String>> request =
                new HttpEntity<>(form, headers);

        ResponseEntity<Map> response =
                restTemplate.postForEntity(TOKEN_URL, request, Map.class);

        return (String) response.getBody().get("access_token");
    }
}
