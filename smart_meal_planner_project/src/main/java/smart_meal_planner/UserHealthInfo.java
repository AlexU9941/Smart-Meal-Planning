import java.util.ArrayList;
import java.util.List;

class UserHealthInfo extends User{
    private int userId;
    private int heightFt;
    private int heightIn;
    private int weight;
    private String sex;
    private String weeklyActivityLevel;
    private List<String> allergies;
    
    // Constructor to initialize all fields
    public UserHealthInfo() {
        this.heightFt = 0;
        this.heightIn = 0;
        this.weight = 0;
        this.sex = "";
        this.weeklyActivityLevel = "";
        this.allergies = new ArrayList<>();
    }

    // Getter to return the values of each field
    public int getUserId() {
        return userId;
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