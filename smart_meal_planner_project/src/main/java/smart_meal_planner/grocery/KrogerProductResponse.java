package smart_meal_planner.grocery;

import java.util.List;

public class KrogerProductResponse {
    private List<Product> data;

    public List<Product> getData() {
    return data;
    }

    public void setData(List<Product> data) {
        this.data = data;
    }

    public static class Product {
        private String productId;
        private String description;
        private List<Image> images;
        private List<Item> items;

        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public List<Image> getImages() {
            return images;
        }

        public void setImages(List<Image> images) {
            this.images = images;
        }

        public List<Item> getItems() {
            return items;
        }

        public void setItems(List<Item> items) {
            this.items = items;
        }
    }


    public static class Image {
        private String perspective;
        private List<Size> sizes;

        public String getPerspective() {
            return perspective;
        }

        public void setPerspective(String perspective) {
            this.perspective = perspective;
        }

        public List<Size> getSizes() {
            return sizes;
        }

        public void setSizes(List<Size> sizes) {
            this.sizes = sizes;
        }
    }

    public static class Size {
        private String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class Item {
        private String size;
        private Price price;

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public Price getPrice() {
            return price;
        }

        public void setPrice(Price price) {
            this.price = price;
        }
    }

    public static class Price {
        private Double regular;

        public Double getRegular() {
            return regular;
        }

        public void setRegular(Double regular) {
            this.regular = regular;
        }
    }
}
