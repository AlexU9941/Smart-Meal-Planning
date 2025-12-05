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

    /* public String getAuthorizationUrl() {
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
    */

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

    /*public OAuthResponse exchangeAuthCode(String authCode) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "authorization_code");
        form.add("code", authCode);
        form.add("redirect_uri", redirectUri);
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);

        return callTokenEndpoint(form);
    }
    */
    /*
    public OAuthResponse exchangeAuthCode(String authCode) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "authorization_code");
        form.add("code", authCode);
        form.add("redirect_uri", redirectUri);

        return callTokenEndpoint(form);
    }
    */

    public OAuthResponse exchangeAuthCode(String authCode) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "authorization_code");
        form.add("code", authCode);
        form.add("redirect_uri", redirectUri);
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);

        return callTokenEndpoint(form);
    }


    /*public OAuthResponse refreshToken(String refreshToken) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "refresh_token");
        form.add("refresh_token", refreshToken);
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);

        return callTokenEndpoint(form);
    }
    
    public OAuthResponse refreshToken(String refreshToken) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "refresh_token");
        form.add("refresh_token", refreshToken);

        return callTokenEndpoint(form);
    }
    */

    public OAuthResponse refreshToken(String refreshToken) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "refresh_token");
        form.add("refresh_token", refreshToken);
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);

        return callTokenEndpoint(form);
    }

    /* private OAuthResponse callTokenEndpoint(MultiValueMap<String, String> form) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<?> entity = new HttpEntity<>(form, headers);

        ResponseEntity<KrogerTokenResponse> res = rest.postForEntity(
                "https://api.kroger.com/v1/connect/oauth2/token",
                entity,
                KrogerTokenResponse.class
        );

        KrogerTokenResponse body = res.getBody();

        OAuthResponse oauth = new OAuthResponse();
        oauth.setAccessToken(body.getAccessToken());
        oauth.setRefreshToken(body.getRefreshToken());
        oauth.setExpiresAt(Instant.now().plusSeconds(body.getExpiresIn()));

        return oauth;
    }
    */

    /*private OAuthResponse callTokenEndpoint(MultiValueMap<String, String> form) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Add Basic Auth
        String credentials = clientId + ":" + clientSecret;
        String encodedCredentials = Base64.getEncoder()
                                        .encodeToString(credentials.getBytes());
        headers.set("Authorization", "Basic " + encodedCredentials);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(form, headers);

        ResponseEntity<KrogerTokenResponse> res = rest.postForEntity(
                "https://api.kroger.com/v1/connect/oauth2/token",
                entity,
                KrogerTokenResponse.class
        );

        KrogerTokenResponse body = res.getBody();
        if (body == null) throw new RuntimeException("Empty token response");

        OAuthResponse oauth = new OAuthResponse();
        oauth.setAccessToken(body.getAccessToken());
        oauth.setRefreshToken(body.getRefreshToken());
        oauth.setExpiresAt(Instant.now().plusSeconds(body.getExpiresIn()));

        return oauth;
    }

    */

    private OAuthResponse callTokenEndpoint(MultiValueMap<String, String> form) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(form, headers);

        ResponseEntity<KrogerTokenResponse> res = rest.exchange(
                "https://api.kroger.com/v1/connect/oauth2/token",
                HttpMethod.POST,
                entity,
                KrogerTokenResponse.class
        );

        KrogerTokenResponse body = res.getBody();
        if (body == null) {
            throw new RuntimeException("Failed to get token from Kroger");
        }

        OAuthResponse oauth = new OAuthResponse();
        oauth.setAccessToken(body.getAccessToken());
        oauth.setRefreshToken(body.getRefreshToken());
        oauth.setExpiresAt(Instant.now().plusSeconds(body.getExpiresIn()));

        return oauth;
    }

    public List<GroceryItem> searchProducts(List<String> terms, String accessToken) {
        List<GroceryItem> out = new ArrayList<>();

        for (String term : terms) {
            String url = UriComponentsBuilder
                    .fromHttpUrl("https://api.kroger.com/v1/products")
                    .queryParam("filter.term", term)
                    .queryParam("filter.locationId", locationId)
                    .queryParam("filter.limit", 10)
                    .build()
                    .toString();

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);

            ResponseEntity<KrogerProductResponse> res = rest.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    KrogerProductResponse.class
            );

            KrogerProductResponse body = res.getBody();
            if (body == null || body.getData() == null) continue;

            for (KrogerProductResponse.Product p : body.getData()) {
                GroceryItem item = new GroceryItem();
                item.setProductId(p.getProductId());
                item.setName(p.getDescription());

                if (p.getItems() != null && !p.getItems().isEmpty()) {
                    KrogerProductResponse.Price pr = p.getItems().get(0).getPrice();
                    item.setPrice(pr != null ? pr.getRegular() : 0.0);
                    item.setSize(p.getItems().get(0).getSize());
                }

                if (p.getImages() != null &&
                    !p.getImages().isEmpty() &&
                    p.getImages().get(0).getSizes() != null &&
                    !p.getImages().get(0).getSizes().isEmpty()) {

                    item.setImageUrl(p.getImages().get(0).getSizes().get(0).getUrl());
                }

                out.add(item);
            }
        }

        return out;
    }

    public String buildCheckoutUrl(List<String> productIds, String accessToken) {
        return "https://www.kroger.com/checkout/start?items=" + String.join(",", productIds);
    }

    public String getProviderName() {
        return "KROGER";
    }

}

        
