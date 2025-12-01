package smart_meal_planner.dto;

public class RecipeRecommendationDTO {
    private int id;
    private String name;
    private String description;
    private String imageUrl;

    public RecipeRecommendationDTO(int id, String name, String description, String imageUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
}
