package smart_meal_planner.grocery;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import smart_meal_planner.dto.GroceryItem;
import smart_meal_planner.dto.OAuthResponse;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/kroger")
public class KrogerController {

    private final KrogerProvider krogerProvider;

    // Store current tokens in memory for simplicity
    private OAuthResponse currentTokens;

    public KrogerController(KrogerProvider krogerProvider) {
        this.krogerProvider = krogerProvider;
    }

    // -------------------------------------------------------------------------
    // STEP 1: Redirect user to Kroger OAuth page
    // -------------------------------------------------------------------------
    @GetMapping("/connect")
    public void connect(HttpServletResponse response) throws IOException {
        String url = krogerProvider.getAuthorizationUrl();
        System.out.println("Redirecting to Kroger OAuth URL: " + url);
        response.sendRedirect(url);
    }

    // -------------------------------------------------------------------------
    // STEP 2: Handle OAuth callback and store tokens
    // -------------------------------------------------------------------------
    @GetMapping("/callback")
    public String oauthCallback(@RequestParam("code") String code) {
        currentTokens = krogerProvider.exchangeAuthCode(code);
        return "<h1>Kroger Connected!</h1><p>You can now close this window.</p>";
    }

    // -------------------------------------------------------------------------
    // Status endpoint to check if connected
    // -------------------------------------------------------------------------
    @GetMapping("/status")
    public Map<String, Boolean> status() {
        boolean connected = currentTokens != null && currentTokens.getAccessToken() != null;
        return Map.of("connected", connected);
    }


    // -------------------------------------------------------------------------
    // STEP 3: Search products (requires valid token)
    // -------------------------------------------------------------------------
    @GetMapping("/search")
    public List<GroceryItem> search(@RequestParam("q") String q) {
        ensureTokenValid();
        return krogerProvider.searchProducts(List.of(q), currentTokens.getAccessToken());
    }



    // -------------------------------------------------------------------------
    // STEP 4: Generate checkout URL for selected product IDs
    // -------------------------------------------------------------------------
    @GetMapping("/checkout")
    public String checkout(@RequestParam List<String> productIds) {
        ensureTokenValid();
        return krogerProvider.buildCheckoutUrl(productIds);
    }

    // -------------------------------------------------------------------------
    // Helper: refresh token if expired
    // -------------------------------------------------------------------------
    /*private void ensureTokenValid() {
        if (currentTokens == null) {
            throw new RuntimeException("Not authenticated with Kroger");
        }

        // Refresh token if expired
        if (Instant.now().isAfter(currentTokens.getExpiresAt())) {
            currentTokens = krogerProvider.refreshToken(currentTokens.getRefreshToken());
        }
    }
        */

    private void ensureTokenValid() {
        if (currentTokens == null || currentTokens.getAccessToken() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated with Kroger");
        }
        if (Instant.now().isAfter(currentTokens.getExpiresAt())) {
            try {
                currentTokens = krogerProvider.refreshToken(currentTokens.getRefreshToken());
            } catch (Exception e) {
                currentTokens = null;
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token refresh failed");
            }
        }
    }
}
