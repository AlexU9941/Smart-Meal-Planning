package smart_meal_planner.grocery;

import lombok.Data;

import java.util.List;
@Data
public class KrogerProductResponse {
    private List<Product> data;

    @Data
    public static class Product {
        private String productId;
        private String upc;
        private List<Description> descriptions;
        private Items items;
    }

    @Data
    public static class Description {
        private String description;
    }

    @Data
    public static class Items {
        private String size;
    }
}
