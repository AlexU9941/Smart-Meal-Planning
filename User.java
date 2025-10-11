import java.util.Date;
import java.util.UUID;


public class User {
    private int userID; //MOST LIKElY will change to be database primary key
    private static int idCounter = 1; 
    private String email;
    private String username;
    private String password; //stores hashed password
    private String fname; 
    private String lname;  
    private Date dob; 
    private String sex; 
    private boolean accountEnabled; 

    private String sessionID; 
    private static int sessionCounter = 1; 
    private boolean isActive; 

    private String salt; //used in password hashing. want to store for verification purposes

    User(String email, String username, String password)
    {
        this.email = email; 
        this.username = username; 
        //this.password = password; 
        
        salt = PasswordUtils.generateSalt(); 
        String hashedPassword = PasswordUtils.hashPassword(password, salt);
        this.password = hashedPassword; 

       //TEMP - will probably change to be database Primary Key 
       userID = idCounter++; //set userId to idCounter value then increment for uniqueness
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

    public String getSex() {
        return sex;
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
        String hashedPassword = PasswordUtils.hashPassword(password, salt);
        this.password = hashedPassword;
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

    public void setSex(String sex)
    {
        this.sex=sex; 
    }


    //called whenever user logs in. 
    public void enableAccount()
    {
        accountEnabled = true; 

        sessionID = UUID.randomUUID().toString(); //generate random String for sessionID
        isActive = true; 
    }


}




