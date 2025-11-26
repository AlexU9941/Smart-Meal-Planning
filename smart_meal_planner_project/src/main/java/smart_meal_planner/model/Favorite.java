package smart_meal_planner.model;

import jakarta.persistence.*;

@Entity
@Table(name = "favorite")
public class Favorite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int recipeId;

    @Column(length = 512)
    private String title;

    @Column(length = 1024)
    private String image;

    @Column(length = 1024)
    private String sourceUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public Favorite() {}

    public Favorite(int recipeId, String title, String image, String sourceUrl, User user) {
        this.recipeId = recipeId;
        this.title = title;
        this.image = image;
        this.sourceUrl = sourceUrl;
        this.user = user;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getRecipeId() { return recipeId; }
    public void setRecipeId(int recipeId) { this.recipeId = recipeId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getSourceUrl() { return sourceUrl; }
    public void setSourceUrl(String sourceUrl) { this.sourceUrl = sourceUrl; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}

