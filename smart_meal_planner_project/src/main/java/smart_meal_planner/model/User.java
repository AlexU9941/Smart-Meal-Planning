package smart_meal_planner.model;
import java.util.Date;
import java.util.UUID;
import jakarta.persistence.*;
import smart_meal_planner.service.PasswordUtils;

@Entity
@Table(name = "user")
public abstract class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long UID;  
    
    //private static int idCounter = 1; 
    
    @Column(nullable = false, unique = true, length = 32)
    private String username;
    
    @Column(name = "user_password", nullable = false, length = 512)
    private String password; //stores hashed password

    @Column(nullable = false, unique = true, length = 255)
    private String email;
    
    @Column(length = 32)
    private String fname; 
   
    @Column(length = 32)
    private String lname;  
    
    @Temporal(TemporalType.DATE)
    private Date dob; 
    
    @Column(length = 512)
    private String salt; //used in password hashing. want to store for verification purposes

    private boolean accountEnabled; 

    private String sessionID; 
    private static int sessionCounter = 1; 
    private boolean isActive; 




    public User() {} // Needed for Spring to map JSON

    public User(String email, String username, String password)
    {
        this.email = email; 
        this.username = username; 
        this.password = password; 
        //this.password = password; 
        
       // salt = PasswordUtils.generateSalt(); 
        //String hashedPassword = PasswordUtils.hashPassword(password, salt);
        //this.password = hashedPassword; 

       //TEMP - will probably change to be database Primary Key 
       //userID = idCounter++; //set userId to idCounter value then increment for uniqueness
    }

    /*GETTERS */
    //will need to modify. sensitive info should not be obtained with get functions
    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }
    public String getFname() {
        return fname;
    }

    public String getLname() {
        return lname;
    }

    public Date getDob() {
        return dob;
    }

    public String getPassword()
    {
        return password; 
    }

    public String getSalt()
    {
        return salt; 
    }
    
    /*SETTERS*/
    //called when the User changes their personal/account information in the application
    public void setEmail(String email)
    {
        this.email = email; 
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        //String hashedPassword = PasswordUtils.hashPassword(password, salt);
        this.password = password;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public void setSalt(String salt)
    {
        this.salt = salt; 
    }


    // public void setSex(String sex)
    // {
    //     this.sex=sex; 
    // }


    //called whenever user logs in. 
    public void enableAccount()
    {
        accountEnabled = true; 

        sessionID = UUID.randomUUID().toString(); //generate random String for sessionID
        isActive = true; 
    }


}




