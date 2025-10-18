package smart_meal_planner.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import smart_meal_planner.model.User;
import smart_meal_planner.service.DatabaseCommunicator;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000") // allow React dev server to connect
public class SignInController
{
    private boolean signInSuccess; //may not need

    @Autowired
    private DatabaseCommunicator dbCommunicator;

    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(HttpServletRequest request, @RequestBody Map<String, String> credentials) {
        try{
            String username = credentials.get("username");
            String password = credentials.get("password");

            User user = dbCommunicator.authenticateUser(username,password);
            signInSuccess = true;  
            request.getSession(true).setAttribute("user", user); //store login status
            return ResponseEntity.ok("Login successful for user: " + user.getUsername());
           
        }
        catch (RuntimeException e) {
               // e.printStackTrace(); // <-- log full exception
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
                //e.printStackTrace(); // <-- log full exception
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server error: " + e.getMessage());
        }
    }

    //may need to rework later
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return ResponseEntity.ok("Logged out");
    }

}