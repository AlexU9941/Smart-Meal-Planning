package smart_meal_planner.dto;

public class GroceryItem {
    private String productId;
    private String name;
    private String brand;
    private double price;
    private String size;              
    private String imageUrl;
    private String aisle;

    // Getters
    public String getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public String getBrand() {
        return brand;
    }

    public double getPrice() {
        return price;
    }

    public String getSize() {
        return size;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getAisle() {
        return aisle;
    }

    // Setters
    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setAisle(String aisle) {
        this.aisle = aisle;
    }
}
