package smart_meal_planner.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import smart_meal_planner.model.User;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class UserController {

    /*etMapping("/users/me")
    public ResponseEntity<?> getLoggedInUser(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not logged in");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());
        return ResponseEntity.ok(response);
    }*/

    @GetMapping("/me")
    public ResponseEntity<?> getLoggedInUser(HttpServletRequest request) {
        try {
            User user = (User) request.getSession().getAttribute("user");
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not logged in");
            }

            Map<String, Object> safeUser = new HashMap<>();
            safeUser.put("email", user.getEmail());
            safeUser.put("username", user.getUsername());
            return ResponseEntity.ok(safeUser);
        } catch (Exception e) {
            e.printStackTrace(); // log full error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to get logged-in user");
        }
    }

}
