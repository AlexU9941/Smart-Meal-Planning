package smart_meal_planner.model;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "User_Health_Info")
@DiscriminatorValue("HEALTH_INFO")
@PrimaryKeyJoinColumn(name = "UID")
public class UserHealthInfo extends User {

    @Column(nullable = false)
    private int heightFt;

    @Column(nullable = false)
    private int heightIn;

    @Column(nullable = false)
    private int weight;

    @Column(nullable = false)
    private String sex;

    @Column(nullable = false)
    private String weeklyActivityLevel;

    @ElementCollection  // correct way to store a list of strings in JPA
    @CollectionTable(
        name = "User_Allergies",
        joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "allergy")
    private List<String> allergies;

    public UserHealthInfo() {}

    // Getters
    public int getHeightFt() { return heightFt; }
    public int getHeightIn() { return heightIn; }
    public int getWeight() { return weight; }
    public String getSex() { return sex; }
    public String getWeeklyActivityLevel() { return weeklyActivityLevel; }
    public List<String> getAllergies() { return allergies; }

    // Setters
    public void setHeightFt(int heightFt) { this.heightFt = heightFt; }
    public void setHeightIn(int heightIn) { this.heightIn = heightIn; }
    public void setWeight(int weight) { this.weight = weight; }
    public void setSex(String sex) { this.sex = sex; }
    public void setWeeklyActivityLevel(String weeklyActivityLevel) { this.weeklyActivityLevel = weeklyActivityLevel; }
    public void setAllergies(List<String> allergies) { this.allergies = allergies; }
}
