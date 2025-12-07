package smart_meal_planner.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import smart_meal_planner.service.DatabaseCommunicator;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "http://localhost:3000")
public class ChangePasswordController {

    @Autowired
    private DatabaseCommunicator userService;

    // Use a static class to map JSON request body
    static class PasswordChangeRequest {
        public String username;
        public String oldPassword;
        public String newPassword;
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestBody PasswordChangeRequest request) {
        try {
            userService.changePassword(request.username, request.oldPassword, request.newPassword);
            return "Password changed successfully";
        } catch (RuntimeException e) {
            return "Error: " + e.getMessage();
        }
    }
}