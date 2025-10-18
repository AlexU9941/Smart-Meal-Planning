package smart_meal_planner.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return ResponseEntity.ok("User logged out successfully.");
    }
}
