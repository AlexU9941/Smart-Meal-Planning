package smart_meal_planner.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import smart_meal_planner.model.User;
import smart_meal_planner.service.DatabaseCommunicator;


@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000") // allow React dev server to connect
public class CreateAccountController {

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
            //e.printStackTrace();
           return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    
    

}
