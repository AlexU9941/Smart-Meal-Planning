package smart_meal_planner.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import smart_meal_planner.model.User;
import smart_meal_planner.model.UserHealthInfo;
import smart_meal_planner.service.UserHealthInfoService;
import smart_meal_planner.repository.UserRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/health-info")
@CrossOrigin(origins = "http://localhost:3000")
public class UserHealthInfoController {

    @Autowired
    private UserHealthInfoService service;

    @Autowired
    private UserRepository userRepository; 

    @PostMapping
    public ResponseEntity<?> addHealthInfo(@RequestBody UserHealthInfo info) {
        // Find user by email
        User user = userRepository.findByEmail(info.getEmail());

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found for email: " + info.getEmail());
        }

        // Link the user to this health info
        info.setUser(user);

        // Save it
        service.saveUserHealthInfo(info);

        return ResponseEntity.ok("Health info saved successfully");
    }

    @GetMapping("/{id}")
    public Optional<UserHealthInfo> getHealthInfo(@PathVariable Long id) {
        return service.getUserHealthInfoById(id);
    }

    @PutMapping("/{id}")
    public UserHealthInfo updateHealthInfo(@PathVariable Long id, @RequestBody UserHealthInfo info) {
        return service.updateUserHealthInfo(id, info);
    }

    @GetMapping
    public ResponseEntity<?> getHealthInfoByEmail(@RequestParam(value = "email", required = false) String email) {
        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body("Email query parameter is required");
        }

        UserHealthInfo info = service.findByEmail(email);
        if (info == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(info);
    }

    @GetMapping("/estimate")
    public ResponseEntity<?> estimateCalories(@RequestParam(value = "email") String email) {
        UserHealthInfo info = service.findByEmail(email);
        if (info == null) return ResponseEntity.notFound().build();

        // compute age
        java.time.LocalDate dob = info.getDateOfBirth();
        int age = 30;
        if (dob != null) {
            age = java.time.Period.between(dob, java.time.LocalDate.now()).getYears();
        }

        double heightCm = info.getHeightFt() * 30.48 + info.getHeightIn() * 2.54;
        double weightKg = info.getWeight() * 0.453592;
        String sex = info.getSex() == null ? "male" : info.getSex().toLowerCase();

        double bmr;
        if (sex.startsWith("f")) {
            bmr = 10 * weightKg + 6.25 * heightCm - 5 * age - 161;
        } else {
            bmr = 10 * weightKg + 6.25 * heightCm - 5 * age + 5;
        }

        String activity = info.getWeeklyActivityLevel() == null ? "sedentary" : info.getWeeklyActivityLevel().toLowerCase();
        double factor;
        switch (activity) {  // traditional switch syntax for Java 8+
            case "light":
                factor = 1.375;
                break;
            case "moderate":
                factor = 1.55;
                break;
            case "active":
                factor = 1.725;
                break;
            case "very active":
                factor = 1.9;
                break;
            default:
                factor = 1.2; // sedentary
        }

        double daily = bmr * factor;
        double weekly = daily * 7;

        Map<String,Object> resp = new HashMap<>();
        resp.put("dailyCalorieGoal", Math.round(daily));
        resp.put("weeklyCalorieGoal", Math.round(weekly));
        resp.put("bmr", Math.round(bmr));
        return ResponseEntity.ok(resp);
    }
}
