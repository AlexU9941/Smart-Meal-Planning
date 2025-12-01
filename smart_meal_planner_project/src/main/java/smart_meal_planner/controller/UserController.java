package smart_meal_planner.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import smart_meal_planner.model.User;
import smart_meal_planner.service.DatabaseCommunicator;
import smart_meal_planner.service.PasswordUtils;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    @Autowired
    private DatabaseCommunicator dbCommunicator;

    // CREATE ACCOUNT
    @PostMapping("/create-account")
    public ResponseEntity<?> createAccount(@RequestBody Map<String, String> userMap) {
        String username = userMap.get("username");
        String email = userMap.get("email");
        String password = userMap.get("password");

        if (dbCommunicator.getUserByEmail(email) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Email already exists");
        }

        try {
            String salt = PasswordUtils.generateSalt();
            String hashedPassword = PasswordUtils.hashPassword(password, salt);

            User newUser = new User();
            newUser.setUsername(username);
            newUser.setEmail(email);
            newUser.setPassword(hashedPassword);
            newUser.setSalt(salt);

            // Optional: enable account/session
            newUser.enableAccount();

            dbCommunicator.saveUser(newUser);

            // Return username/email for frontend popup
            return ResponseEntity.ok(Map.of(
                    "username", newUser.getUsername(),
                    "email", newUser.getEmail()
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to create user");
        }
    }

    // SIGN IN (can reuse from SignInController)
    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(@RequestBody Map<String, String> credentials) {
        try {
            String username = credentials.get("username");
            String password = credentials.get("password");

            User user = dbCommunicator.authenticateUser(username, password);

            Map<String, Object> response = Map.of(
                    "username", user.getUsername(),
                    "email", user.getEmail()
            );

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server error: " + e.getMessage());
        }
    }
}
