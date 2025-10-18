package smart_meal_planner;
import jakarta.persistence.*;
import java.util.List;
import java.util.ArrayList;


@Entity
@Table(name = "User_Health_Info") class UserHealthInfo extends User {
    @Id
    private long uid;
    
    @Column(nullable=false)
    private int heightFt;
    
    @Column(nullable=false)
    private int heightIn;
    
    @Column(nullable=false)
    private int weight;

    @Column(nullable=false, length=10)
    private String sex;

    @Column(nullable=false, length=50)
    private String weeklyActivityLevel;

    @Column(legnth=100)
    private List<String> allergies;
    
    // Constructor to initialize all fields
    public UserHealthInfo() {}

    // Getter to return the values of each field
    public int getUserId() {
        return uid;
    }
    public int getHeightFt() {
        return heightFt;
    }   
    public int getHeightIn() {
        return heightIn;
    }
    public int getWeight() {
        return weight;
    }
    public String getSex() {
        return sex;
    }
    public String getWeeklyActivityLevel() {
        return weeklyActivityLevel;
    }
    public List<String> getAllergies() {
        return allergies;
    }

    // Setters to set the values of each field
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public void setHeightFt(int heightFt) {
        this.heightFt = heightFt;
    }
    public void setHeightIn(int heightIn) {
        this.heightIn = heightIn;
    }
    public void setWeight(int weight) {
        this.weight = weight;
    }
    public void setSex(String sex) {
        this.sex = sex;
    }
    public void setWeeklyActivityLevel(String weeklyActivityLevel) {
        this.weeklyActivityLevel = weeklyActivityLevel;
    }
    public void setAllergies(List<String> allergies) {
        this.allergies = allergies;
    }
}