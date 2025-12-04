package smart_meal_planner.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Table;

@Entity
public class UserCreatedRecipe {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String title; 

    @Column(length = 10000)
    private String recipeContent; 

    
    private Integer servings; 
    private Integer prepMinutes; 

    public UserCreatedRecipe() { }

    public UserCreatedRecipe(Long userId, String title, String recipeContent, Integer servings, Integer prepMinutes) {
        this.userId = userId;
        this.title = title;
        this.recipeContent = recipeContent;
        this.servings = servings;
        this.prepMinutes = prepMinutes;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getUserId() {
        return userId;
    }
    @JsonProperty("userId")
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getRecipeContent() {
        return recipeContent;
    }
    public void setRecipeContent(String recipeContent) {
        this.recipeContent = recipeContent;
    }
    public Integer getServings() {
        return servings;
    }
    public void setServings(Integer servings) {
        this.servings = servings;
    }
    public Integer getPrepMinutes() {
        return prepMinutes;
    }
    public void setPrepMinutes(Integer prepMinutes) {
        this.prepMinutes = prepMinutes;
    }

}