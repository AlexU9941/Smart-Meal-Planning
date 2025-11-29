package smart_meal_planner.grocery;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import smart_meal_planner.dto.GroceryItem;
import smart_meal_planner.dto.OAuthResponse;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/kroger")
public class KrogerController {

    private final KrogerProvider krogerProvider;

    // For testing, we store tokens in memory. In production, store per user in DB
    private OAuthResponse currentTokens;

    public KrogerController(KrogerProvider krogerProvider) {
        this.krogerProvider = krogerProvider;
    }

    // -------------------------------------------------------------
    // STEP 1: Redirect browser to Kroger OAuth login
    // -------------------------------------------------------------
    @GetMapping("/connect")
    public void connect(HttpServletResponse response) throws IOException {
        response.sendRedirect(krogerProvider.getAuthorizationUrl());
    }

    // -------------------------------------------------------------
    // STEP 2: Kroger redirects back here with ?code=xxxx
    // -------------------------------------------------------------
    @GetMapping("/callback")
    public String oauthCallback(@RequestParam String code) {
        currentTokens = krogerProvider.exchangeAuthCode(code);
        return "<h1>Kroger Connected!</h1><p>You can now close this window.</p>";
    }

    // -------------------------------------------------------------
    // STEP 3: Check if user is connected
    // -------------------------------------------------------------
    @GetMapping("/status")
    public boolean status() {
        return currentTokens != null && currentTokens.getAccessToken() != null;
    }

    // -------------------------------------------------------------
    // STEP 4: Search products (requires OAuth)
    // -------------------------------------------------------------
    @GetMapping("/search")
    public List<GroceryItem> search(@RequestParam List<String> q) {
        ensureTokenValid();
        return krogerProvider.searchProducts(q, currentTokens.getAccessToken());
    }

    // -------------------------------------------------------------
    // STEP 5: Build checkout URL
    // -------------------------------------------------------------
    @GetMapping("/checkout")
    public String checkout(@RequestParam List<String> productIds) {
        ensureTokenValid();
        return krogerProvider.buildCheckoutUrl(productIds, currentTokens.getAccessToken());
    }

    // -------------------------------------------------------------
    // Helper: Refresh token if expired
    // -------------------------------------------------------------
    private void ensureTokenValid() {
        if (currentTokens == null) {
            throw new RuntimeException("Not authenticated with Kroger");
        }

        // Refresh token if expired
        if (Instant.now().isAfter(currentTokens.getExpiresAt())) {
            currentTokens = krogerProvider.refreshToken(currentTokens.getRefreshToken());
        }
    }
}
