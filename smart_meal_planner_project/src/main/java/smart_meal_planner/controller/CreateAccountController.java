package smart_meal_planner.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import smart_meal_planner.model.User;
import smart_meal_planner.service.DatabaseCommunicator;


@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000") // allow your React dev server to connect
public class CreateAccountController {
    // private String attemptUsername; 
    // private String attemptPassword;
    // private String attemptEmail;
    // private String attemptFName;
    // private String attemptLName; 
    // private Date attemptDOB; 
    // private boolean createAccountSuccess; 

    @Autowired
    private DatabaseCommunicator dbCommunicator;

    @PostMapping("/create-account")
    public ResponseEntity<?> createAccount(@RequestBody User user) {
        try {
            System.out.println("Received new account: " + user.getUsername() + ", " + user.getEmail());

            // Save the user (validation and duplicate checks happen in DatabaseCommunicator)
            User savedUser = dbCommunicator.saveUser(user);

            return new ResponseEntity<>(savedUser, HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            // Invalid input (like empty username/email)
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            // Duplicate username/email or other rule violation
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            // Unexpected issue
            e.printStackTrace();
           return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    
    // //NEED TO FINISH LATER
    // private boolean validateAccountCreation()
    // {
    //     /* 
    //      * Method communicate with DatabaseCommunicator 
    //      * Check Username,Password, Email information - 
    //      * if unique, can set createAccountSuccess to true and store 
    //      * values (fname,lname,dob)
    //      * 
    //      * If username/email already in use, reject attempt to create 
    //      * an account by setting createAccountSuccess to false. 
    //     */


    //     return createAccountSuccess;
    // }



     
    // /*
    //  * setter functions, utilized by front-end to set values used 
    //  * to attempt account creation.
    // */
    // public void setAttemptUsername(String attemptUsername) {
    //     this.attemptUsername = attemptUsername;
    // }
    // public void setAttemptPassword(String attemptPassword) {
    //     this.attemptPassword = attemptPassword;
    // }
    // public void setAttemptEmail(String attemptEmail) {
    //     this.attemptEmail = attemptEmail;
    // }
    // public void setAttemptFName(String attemptFName) {
    //     this.attemptFName = attemptFName;
    // }
    // public void setAttemptLName(String attemptLName) {
    //     this.attemptLName = attemptLName;
    // }
    // public void setAttemptDOB(Date attemptDOB) {
    //     this.attemptDOB = attemptDOB;
    // }

}
