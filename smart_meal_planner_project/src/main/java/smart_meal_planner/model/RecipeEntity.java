package smart_meal_planner.model;
import jakarta.persistence.*;

@Entity
@Table(name = "recipes")
public class RecipeEntity {

    @Id
    private Long id;  // Spoonacular recipe ID

    private String title;

    @Column(length = 2000)
    private String image;

    private double readyInMinutes;

    private double servings;

   // @Column(length = 2000)
   //private String summary;

    public RecipeEntity() {}

    public RecipeEntity(Long id, String title, String image, double readyInMinutes, double servings) {
        this.id = id;
        this.title = title;
        this.image = image;
        this.readyInMinutes = readyInMinutes;
        this.servings = servings;
      //  this.summary = summary;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public double getReadyInMinutes() { return readyInMinutes; }
    public void setReadyInMinutes(double readyInMinutes) { this.readyInMinutes = readyInMinutes; }

    public double getServings() { return servings; }
    public void setServings(double servings) { this.servings = servings; }

   // public String getSummary() { return summary; }
    //public void setSummary(String summary) { this.summary = summary; }
}