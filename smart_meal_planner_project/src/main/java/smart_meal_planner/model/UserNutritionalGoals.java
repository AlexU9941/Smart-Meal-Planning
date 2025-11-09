package smart_meal_planner.model;
import jakarta.persistence.*;

@Entity
@Table(name = "user_nutrition_goals")
public class UserNutritionalGoals {
    
    @Id
    private Long uid; 

    @OneToOne
    @MapsId
    @JoinColumn(name="uid")
    private User user; 

    //prioritized goals - will receiver higher scoring 
    @Column(nullable = true)
    private double dailyCaloriesGoal; 
     
    @Column(nullable = true)
    private double dailyProteinGoal; 
    
    @Column(nullable = true)
    private double dailyFatGoal; 

    @Column(nullable = true)
    private double dailyCarbohydratesGoal; 

    //will be scored less in meal plan generation
    @Column(nullable = true)
    private double dailySaturatedFatGoal; 

    @Column(nullable = true)
    private double dailySugarGoal; 

    @Column(nullable = true)
    private double dailyCholesterolGoal; 

    @Column(nullable = true)
    private double dailySodiumGoal; 

    //connect to user via email
    @Column(nullable = false, unique = true, length = 255)
    private String email;



    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public double getDailyCaloriesGoal() {
        return dailyCaloriesGoal;
    }

    public void setDailyCaloriesGoal(double dailyCaloriesGoal) {
        this.dailyCaloriesGoal = dailyCaloriesGoal;
    }

    public double getDailyProteinGoal() {
        return dailyProteinGoal;
    }

    public void setDailyProteinGoal(double dailyProteinGoal) {
        this.dailyProteinGoal = dailyProteinGoal;
    }

    public double getDailyFatGoal() {
        return dailyFatGoal;
    }

    public void setDailyFatGoal(double dailyFatGoal) {
        this.dailyFatGoal = dailyFatGoal;
    }

    public double getDailyCarbohydratesGoal() {
        return dailyCarbohydratesGoal;
    }

    public void setDailyCarbohydratesGoal(double dailyCarbohydratesGoal) {
        this.dailyCarbohydratesGoal = dailyCarbohydratesGoal;
    }

    public double getDailySaturatedFatGoal() {
        return dailySaturatedFatGoal;
    }

    public void setDailySaturatedFatGoal(double dailySaturatedFatGoal) {
        this.dailySaturatedFatGoal = dailySaturatedFatGoal;
    }

    public double getDailySugarGoal() {
        return dailySugarGoal;
    }

    public void setDailySugarGoal(double dailySugarGoal) {
        this.dailySugarGoal = dailySugarGoal;
    }

    public double getDailyCholesterolGoal() {
        return dailyCholesterolGoal;
    }

    public void setDailyCholesterolGoal(double dailyCholesterolGoal) {
        this.dailyCholesterolGoal = dailyCholesterolGoal;
    }

    public double getDailySodiumGoal() {
        return dailySodiumGoal;
    }

    public void setDailySodiumGoal(double dailySodiumGoal) {
        this.dailySodiumGoal = dailySodiumGoal;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


}
