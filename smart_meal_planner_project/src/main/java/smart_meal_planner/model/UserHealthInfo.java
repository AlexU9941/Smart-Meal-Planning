package smart_meal_planner.model;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "User_Health_Info")
//@DiscriminatorValue("HEALTH_INFO")
// @PrimaryKeyJoinColumn(name = "UID")
//public class UserHealthInfo extends User {  //don't extend user 
public class UserHealthInfo {

    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uid;

    @OneToOne
    @MapsId // tells Hibernate to use the User's ID as this entity's ID
    @JoinColumn(name = "uid")
    private User user;

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

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    //connect to user via email
    @Column(nullable = false, unique = true, length = 255)
    private String email;

    public UserHealthInfo() {}

    // Getters
    public int getHeightFt() { return heightFt; }
    public int getHeightIn() { return heightIn; }
    public int getWeight() { return weight; }
    public String getSex() { return sex; }
    public String getWeeklyActivityLevel() { return weeklyActivityLevel; }
    public List<String> getAllergies() { return allergies; }
    public User getUser() {return user; }
    public String getEmail() {return email;} 
    public LocalDate getDateOfBirth() {return dateOfBirth;}


    // Setters
    public void setHeightFt(int heightFt) { this.heightFt = heightFt; }
    public void setHeightIn(int heightIn) { this.heightIn = heightIn; }
    public void setWeight(int weight) { this.weight = weight; }
    public void setSex(String sex) { this.sex = sex; }
    public void setWeeklyActivityLevel(String weeklyActivityLevel) { this.weeklyActivityLevel = weeklyActivityLevel; }
    public void setAllergies(List<String> allergies) { this.allergies = allergies; }
    public void setUser(User user) {this.user = user;  }
    public void setEmail(String email) {this.email = email;}
    public void setDateOfBirth(LocalDate dateOfBirth) {this.dateOfBirth = dateOfBirth;}
}
