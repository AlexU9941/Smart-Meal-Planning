
import java.util.Date;


public class CreateAccountController {
    private String attemptUsername; 
    private String attemptPassword;
    private String attemptEmail;
    private String attemptFName;
    private String attemptLName; 
    private Date attemptDOB; 
    private boolean createAccountSuccess; 


 
    /*
     * setter functions, utilized by front-end to set values used 
     * to attempt account creation.
    */
    public void setAttemptUsername(String attemptUsername) {
        this.attemptUsername = attemptUsername;
    }
    public void setAttemptPassword(String attemptPassword) {
        this.attemptPassword = attemptPassword;
    }
    public void setAttemptEmail(String attemptEmail) {
        this.attemptEmail = attemptEmail;
    }
    public void setAttemptFName(String attemptFName) {
        this.attemptFName = attemptFName;
    }
    public void setAttemptLName(String attemptLName) {
        this.attemptLName = attemptLName;
    }
    public void setAttemptDOB(Date attemptDOB) {
        this.attemptDOB = attemptDOB;
    }
    
    //NEED TO FINISH LATER
    private boolean validateAccountCreation()
    {
        /* 
         * Method communicate with DatabaseCommunicator 
         * Check Username,Password, Email information - 
         * if unique, can set createAccountSuccess to true and store 
         * values (fname,lname,dob)
         * 
         * If username/email already in use, reject attempt to create 
         * an account by setting createAccountSuccess to false. 
        */


        return createAccountSuccess;
    }


}
