package smart_meal_planner.grocery;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
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

    @Value("${kroger.apiBaseUrl}")
    private String apiBaseUrl;

    @Value("${kroger.oauth2BaseUrl}")
    private String oauth2BaseUrl;

    public String getAuthorizationUrl() {
        return UriComponentsBuilder
                .fromHttpUrl(oauth2BaseUrl+"/authorize")
                .queryParam("scope", "product.compact")
                .queryParam("response_type", "code")
                .queryParam("client_id", clientId)
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
    
    public OAuthResponse exchangeAuthCode(String authCode) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "authorization_code");
        form.add("code", authCode);
        form.add("redirect_uri", redirectUri);

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
    */

    public OAuthResponse refreshToken(String refreshToken) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "refresh_token");
        form.add("refresh_token", refreshToken);

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
    
    /* private OAuthResponse callTokenEndpoint(MultiValueMap<String, String> form) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // REQUIRED: Basic Auth with client_id:client_secret (Kroger rejects without this)
        String credentials = clientId + ":" + clientSecret;
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
        headers.set("Authorization", "Basic " + encodedCredentials);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(form, headers);

        try {
            // Use the fixed Kroger token URL (don't rely on oauth2BaseUrl if it's misconfigured)
            String tokenUrl = "https://api.kroger.com/v1/connect/oauth2/token";
            ResponseEntity<KrogerTokenResponse> response = rest.exchange(
                tokenUrl,
                HttpMethod.POST,
                entity,
                KrogerTokenResponse.class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                System.err.println("Token response status: " + response.getStatusCode());
                System.err.println("Response body: " + response.getBody());
                throw new RuntimeException("Kroger token endpoint returned: " + response.getStatusCode());
            }

            KrogerTokenResponse body = response.getBody();
            if (body == null || body.getAccessToken() == null) {
                throw new RuntimeException("Invalid token response from Kroger: no access_token");
            }

            OAuthResponse oauth = new OAuthResponse();
            oauth.setAccessToken(body.getAccessToken());
            oauth.setRefreshToken(body.getRefreshToken());
            oauth.setExpiresAt(Instant.now().plusSeconds(body.getExpiresIn() - 60)); // 1-min safety buffer

            return oauth;
        } catch (HttpClientErrorException e) {
            System.err.println("HTTP error details: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            throw new RuntimeException("Kroger token exchange failed: " + e.getStatusCode() + " - " + e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("Unexpected token error: " + e.getMessage());
            throw new RuntimeException("Kroger token exchange failed: " + e.getMessage(), e);
        }
    }

    */

    private OAuthResponse callTokenEndpoint(MultiValueMap<String, String> form) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // CRITICAL: Basic Auth header (Kroger rejects 401 without this)
        String credentials = clientId + ":" + clientSecret;  // e.g., "smartmealplannerapp-bbc92fl7:your_secret"
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
        headers.set("Authorization", "Basic " + encodedCredentials);

        // Debug: Log the exact header value (redact in production)
        System.out.println("=== KROGER TOKEN REQUEST DEBUG ===");
        System.out.println("Client ID: " + clientId.substring(0, 10) + "...");  // Partial for safety
        System.out.println("Credentials (encoded): " + encodedCredentials.substring(0, 20) + "...");  // Partial
        System.out.println("Full Auth Header: Basic " + encodedCredentials);  // Full for debug (remove later)
        System.out.println("Form Data: " + form.toSingleValueMap());  // e.g., {grant_type=authorization_code, code=abc123, redirect_uri=http://...}
        System.out.println("=====================================");

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(form, headers);

        String tokenUrl = "https://api.kroger.com/v1/connect/oauth2/token";  // Hardcoded for reliability

        try {
            ResponseEntity<KrogerTokenResponse> response = rest.exchange(
                tokenUrl, HttpMethod.POST, entity, KrogerTokenResponse.class
            );

            // Log response status
            System.out.println("Token Response Status: " + response.getStatusCode());

            KrogerTokenResponse body = response.getBody();
            if (!response.getStatusCode().is2xxSuccessful() || body == null || body.getAccessToken() == null) {
                System.err.println("Full Response Body: " + (body != null ? body.toString() : "EMPTY"));
                throw new RuntimeException("Kroger token failed: Status " + response.getStatusCode() + " - Body: " + (body != null ? body.toString() : "EMPTY"));
            }

            OAuthResponse oauth = new OAuthResponse();
            oauth.setAccessToken(body.getAccessToken());
            oauth.setRefreshToken(body.getRefreshToken());
            oauth.setExpiresAt(Instant.now().plusSeconds(body.getExpiresIn() - 60));  // Safety buffer

            System.out.println("SUCCESS: Tokens obtained! Expires in: " + body.getExpiresIn() + "s");
            return oauth;

        } catch (HttpClientErrorException e) {
            System.err.println("KROGER 401 DETAILS:");
            System.err.println("Status: " + e.getStatusCode());
            System.err.println("Response Body: " + e.getResponseBodyAsString());
            System.err.println("Request Headers Sent: " + headers);  // Confirms Basic Auth was included
            e.printStackTrace();  // Full stack for debugging
            throw new RuntimeException("Kroger token exchange failed: " + e.getStatusCode() + " - " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            System.err.println("Unexpected token error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Kroger token exchange failed: " + e.getMessage(), e);
        }
    }
    /*
    private OAuthResponse callTokenEndpoint(MultiValueMap<String, String> form) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // BASIC AUTH ONLY (required by Kroger)
        String credentials = clientId + ":" + clientSecret;
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
        headers.set("Authorization", "Basic " + encodedCredentials);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(form, headers);

        ResponseEntity<KrogerTokenResponse> res = rest.postForEntity(
            oauth2BaseUrl+"/token",
                entity,
                KrogerTokenResponse.class
        );

        KrogerTokenResponse body = res.getBody();
        if (body == null) throw new RuntimeException("Token response empty");

        OAuthResponse oauth = new OAuthResponse();
        oauth.setAccessToken(body.getAccessToken());
        oauth.setRefreshToken(body.getRefreshToken());
        oauth.setExpiresAt(Instant.now().plusSeconds(body.getExpiresIn()));

        return oauth;
    }
    */
    public List<GroceryItem> searchProducts(List<String> terms, String accessToken) {
        List<GroceryItem> out = new ArrayList<>();

        for (String term : terms) {
            String url = UriComponentsBuilder
                    .fromHttpUrl(apiBaseUrl+"/v1/products")
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
