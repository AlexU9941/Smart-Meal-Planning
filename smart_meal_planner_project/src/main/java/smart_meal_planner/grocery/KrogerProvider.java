package smart_meal_planner.grocery;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import smart_meal_planner.dto.GroceryItem;
import smart_meal_planner.dto.KrogerTokenResponse;
import smart_meal_planner.dto.OAuthResponse;
import smart_meal_planner.grocery.KrogerProductResponse.Product;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Component
public class KrogerProvider {

    private final RestTemplate rest = new RestTemplate();

    @Value("${kroger.clientId}")
    private String clientId;

    @Value("${kroger.clientSecret}")
    private String clientSecret;

    @Value("${kroger.redirectUri}")
    private String redirectUri;

    @Value("${kroger.locationId}")
    private String locationId;

    // -------------------------------------------------------------------------
    // STEP 1: Generate authorization URL
    // -------------------------------------------------------------------------
    public String getAuthorizationUrl() {
        return UriComponentsBuilder
                .fromHttpUrl("https://api.kroger.com/v1/connect/oauth2/authorize")
                .queryParam("client_id", clientId)
                .queryParam("response_type", "code")
                .queryParam("scope", "product.compact")
                .queryParam("redirect_uri", redirectUri)
                .queryParam("state", "abc123")
                .build(true)
                .toUriString();
    }

    // -------------------------------------------------------------------------
    // STEP 2: Exchange authorization code for tokens
    // -------------------------------------------------------------------------
    public OAuthResponse exchangeAuthCode(String authCode) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "authorization_code");
        form.add("code", authCode);
        form.add("redirect_uri", redirectUri);

        // Do NOT add client_id/client_secret in the body (use Basic Auth header)
        return callTokenEndpoint(form);
    }

    // -------------------------------------------------------------------------
    // Refresh access token using refresh token
    // -------------------------------------------------------------------------
    public OAuthResponse refreshToken(String refreshToken) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "refresh_token");
        form.add("refresh_token", refreshToken);

        // Do NOT add client_id/client_secret in the body (use Basic Auth header)
        return callTokenEndpoint(form);
    }

    // -------------------------------------------------------------------------
    // Internal helper: call token endpoint with Basic Auth
    // -------------------------------------------------------------------------
    private OAuthResponse callTokenEndpoint(MultiValueMap<String, String> form) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Basic Auth header
        String credentials = clientId + ":" + clientSecret;
        String encoded = Base64.getEncoder().encodeToString(credentials.getBytes());
        headers.set("Authorization", "Basic " + encoded);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(form, headers);

        ResponseEntity<KrogerTokenResponse> res = rest.postForEntity(
                "https://api.kroger.com/v1/connect/oauth2/token",
                entity,
                KrogerTokenResponse.class
        );

        KrogerTokenResponse body = res.getBody();
        if (body == null) {
            throw new RuntimeException("Empty token response from Kroger");
        }

        OAuthResponse oauth = new OAuthResponse();
        oauth.setAccessToken(body.getAccessToken());
        oauth.setRefreshToken(body.getRefreshToken());
        oauth.setExpiresAt(Instant.now().plusSeconds(body.getExpiresIn()));

        return oauth;
    }

    // -------------------------------------------------------------------------
    // STEP 3: Search products
    // -------------------------------------------------------------------------

    public List<GroceryItem> searchProducts(List<String> keywords, String accessToken) {
        List<GroceryItem> items = new ArrayList<>();

        for (String keyword : keywords) {
            String url = UriComponentsBuilder
                    .fromHttpUrl("https://api.kroger.com/v1/products")
                    .queryParam("filter.term", keyword)
                    .queryParam("filter.locationId", locationId)
                    .queryParam("filter.limit", 10)
                    .build(true)
                    .toUriString();

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            HttpEntity<?> entity = new HttpEntity<>(headers);

            ResponseEntity<KrogerProductResponse> response = rest.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    KrogerProductResponse.class
            );

            KrogerProductResponse body = response.getBody();
            if (body == null || body.getData() == null) continue;

            for (Product product : body.getData()) {
                GroceryItem g = new GroceryItem();
                g.setName(product.getDescription());
                g.setProductId(product.getProductId());

                if (product.getImages() != null && !product.getImages().isEmpty() &&
                    product.getImages().get(0).getSizes() != null &&
                    !product.getImages().get(0).getSizes().isEmpty()) {
                    g.setImageUrl(product.getImages().get(0).getSizes().get(0).getUrl());
                }

                items.add(g);
            }
        }

        return items;
    }


    // -------------------------------------------------------------------------
    // STEP 4: Build checkout URL
    // -------------------------------------------------------------------------
    public String buildCheckoutUrl(List<String> productIds) {
        return UriComponentsBuilder
                .fromHttpUrl("https://www.kroger.com/cart/add")
                .queryParam("items", String.join(",", productIds))
                .build(true)
                .toUriString();
    }

    public String getProviderName() {
        return "KROGER";
    }
}
