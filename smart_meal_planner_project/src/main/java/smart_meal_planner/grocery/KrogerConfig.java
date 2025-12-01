package smart_meal_planner.grocery;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KrogerConfig {

    @Value("${kroger.clientId}")
    public String clientId;

    @Value("${kroger.clientSecret}")
    public String clientSecret;

    @Value("${kroger.redirectUri}")
    public String redirectUri;

    @Value("${kroger.locationId}")
    public String locationId;

    @Value("${kroger.tokenUrl}")
    public String tokenUrl;

    @Value("${kroger.productUrl}")
    public String productUrl;

    @Value("${kroger.scope}")
    public String scope;
}
