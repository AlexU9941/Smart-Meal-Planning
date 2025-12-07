package smart_meal_planner.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletResponse;
import smart_meal_planner.dto.GroceryItem;
import smart_meal_planner.dto.OAuthResponse;
import smart_meal_planner.grocery.KrogerController;
import smart_meal_planner.grocery.KrogerProvider;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class KrogerControllerTest {

    private KrogerProvider krogerProvider;
    private KrogerController controller;

    @BeforeEach
    void setUp() {
        krogerProvider = mock(KrogerProvider.class);
        controller = new KrogerController(krogerProvider);
    }

    // ----- connect() -----
    @Test
    void testConnectRedirects() throws IOException {
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(krogerProvider.getAuthorizationUrl()).thenReturn("https://auth.url");

        controller.connect(response);

        verify(response, times(1)).sendRedirect("https://auth.url");
    }

    @Test
    void testConnectThrowsIOException() throws IOException {
        HttpServletResponse response = mock(HttpServletResponse.class);
        doThrow(new IOException("IO error")).when(response).sendRedirect(anyString());

        assertThrows(IOException.class, () -> controller.connect(response));
    }

    // ----- oauthCallback() -----
    @Test
    void testOauthCallbackStoresToken() {
        OAuthResponse token = new OAuthResponse();
        token.setAccessToken("abc");
        token.setRefreshToken("refresh");
        token.setExpiresAt(Instant.now().plusSeconds(3600));

        when(krogerProvider.exchangeAuthCode("code123")).thenReturn(token);

        String result = controller.oauthCallback("code123");
        assertTrue(controller.status().get("connected"));
        assertEquals("<h1>Kroger Connected!</h1><p>You can now close this window.</p>", result);
    }

    // ----- status() -----
    @Test
    void testStatusConnectedAndDisconnected() {
        assertFalse(controller.status().get("connected")); // no token

        OAuthResponse token = new OAuthResponse();
        token.setAccessToken("abc");
        token.setExpiresAt(Instant.now().plusSeconds(3600));
        controller.currentTokens = token;

        assertTrue(controller.status().get("connected"));
    }

    // ----- search() -----
    @Test
    void testSearchHappyPath() {
        OAuthResponse token = new OAuthResponse();
        token.setAccessToken("abc");
        token.setExpiresAt(Instant.now().plusSeconds(3600));
        controller.currentTokens = token;

        GroceryItem item = new GroceryItem();
        item.setName("Apple");
        when(krogerProvider.searchProducts(List.of("apple"), "abc")).thenReturn(List.of(item));

        List<GroceryItem> results = controller.search("apple");
        assertEquals(1, results.size());
        assertEquals("Apple", results.get(0).getName());
    }

    @Test
    void testSearchUnauthorizedNoToken() {
        controller.currentTokens = null;
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> controller.search("apple"));
        assertEquals(401, ex.getStatus().value());
    }

    @Test
    void testSearchTokenExpiredRefreshSuccess() {
        OAuthResponse expired = new OAuthResponse();
        expired.setAccessToken("old");
        expired.setRefreshToken("refresh123");
        expired.setExpiresAt(Instant.now().minusSeconds(10));
        controller.currentTokens = expired;

        OAuthResponse newToken = new OAuthResponse();
        newToken.setAccessToken("new");
        newToken.setExpiresAt(Instant.now().plusSeconds(3600));

        when(krogerProvider.refreshToken("refresh123")).thenReturn(newToken);
        when(krogerProvider.searchProducts(List.of("apple"), "new")).thenReturn(List.of());

        List<GroceryItem> results = controller.search("apple");
        assertEquals("new", controller.currentTokens.getAccessToken());
    }

    @Test
    void testSearchTokenExpiredRefreshFails() {
        OAuthResponse expired = new OAuthResponse();
        expired.setAccessToken("old");
        expired.setRefreshToken("refresh123");
        expired.setExpiresAt(Instant.now().minusSeconds(10));
        controller.currentTokens = expired;

        when(krogerProvider.refreshToken("refresh123")).thenThrow(new RuntimeException("fail"));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> controller.search("apple"));
        assertEquals(401, ex.getStatus().value());
        assertNull(controller.currentTokens);
    }

    // ----- checkout() -----
    @Test
    void testCheckoutHappyPath() {
        OAuthResponse token = new OAuthResponse();
        token.setAccessToken("abc");
        token.setExpiresAt(Instant.now().plusSeconds(3600));
        controller.currentTokens = token;

        when(krogerProvider.buildCheckoutUrl(List.of("id1", "id2"), "abc")).thenReturn("https://checkout.url");

        String url = controller.checkout(List.of("id1", "id2"));
        assertEquals("https://checkout.url", url);
    }

    @Test
    void testCheckoutUnauthorized() {
        controller.currentTokens = null;
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> controller.checkout(List.of("id1")));
        assertEquals(401, ex.getStatus().value());
    }
}
