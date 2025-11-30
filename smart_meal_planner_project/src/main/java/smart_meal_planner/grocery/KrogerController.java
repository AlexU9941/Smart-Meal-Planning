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

    private OAuthResponse currentTokens;

    public KrogerController(KrogerProvider krogerProvider) {
        this.krogerProvider = krogerProvider;
    }


    @GetMapping("/connect")
    public void connect(HttpServletResponse response) throws IOException {
        System.out.println(krogerProvider.getAuthorizationUrl());
        response.sendRedirect(krogerProvider.getAuthorizationUrl());
    }


    @GetMapping("/callback")
    public String oauthCallback(@RequestParam("code") String code) {
        currentTokens = krogerProvider.exchangeAuthCode(code);
        return "<h1>Kroger Connected!</h1><p>You can now close this window.</p>";
    }


    @GetMapping("/status")
    public boolean status() {
        return currentTokens != null && currentTokens.getAccessToken() != null;
    }

    
    @GetMapping("/search")
    public List<GroceryItem> search(@RequestParam List<String> q) {
        ensureTokenValid();
        return krogerProvider.searchProducts(q, currentTokens.getAccessToken());
    }

    
    @GetMapping("/checkout")
    public String checkout(@RequestParam List<String> productIds) {
        ensureTokenValid();
        return krogerProvider.buildCheckoutUrl(productIds, currentTokens.getAccessToken());
    }

    
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
