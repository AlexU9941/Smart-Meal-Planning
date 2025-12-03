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

@RestController
@RequestMapping("/api/health-info")
@CrossOrigin(origins = "http://localhost:3000")
public class UserHealthInfoController {

    @Autowired
    private UserHealthInfoService service;

    @Autowired
    private UserRepository userRepository;

    // -------------------- POST: Create new health info --------------------
    @PostMapping
    public ResponseEntity<?> addHealthInfo(@RequestBody UserHealthInfo info) {
        User user = userRepository.findByEmail(info.getEmail());
        if (user == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found for email: " + info.getEmail());

        if (service.findByEmail(info.getEmail()) != null)
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Health info already exists for this user");

        info.setUser(user);
        info.setEmail(user.getEmail());
        info.setUid(user.getUID()); // Required because @MapsId

        service.saveUserHealthInfo(info);
        return ResponseEntity.ok("Health info saved successfully");
    }

    // -------------------- GET: Fetch health info by email or username --------------------
    @GetMapping
    public ResponseEntity<?> getHealthInfo(
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "username", required = false) String username) {

        UserHealthInfo info = null;

        if (email != null && !email.isEmpty()) {
            info = service.findByEmail(email);
        } else if (username != null && !username.isEmpty()) {
            User u = userRepository.findByUsername(username);
            if (u != null)
                info = service.findByUserUID(u.getUID());
        }

        if (info == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(info);
    }

    // -------------------- PUT: Update profile fields only --------------------
    @PutMapping("/profile/{email}")
    public ResponseEntity<?> updateProfile(
            @PathVariable String email,
            @RequestBody UserHealthInfo updated) {

        UserHealthInfo existing = service.findByEmail(email);
        if (existing == null)
            return ResponseEntity.notFound().build();

        // Only update profile fields (bio, theme, picture)
        existing.setBio(updated.getBio());
        existing.setTheme(updated.getTheme());
        existing.setPicture(updated.getPicture());

        service.saveUserHealthInfo(existing);
        return ResponseEntity.ok("Profile updated successfully");
    }

    // -------------------- PUT: Update full health info by ID --------------------
    @PutMapping("/{id}")
    public ResponseEntity<?> updateHealthInfo(
            @PathVariable Long id,
            @RequestBody UserHealthInfo info) {

        try {
            UserHealthInfo updated = service.updateUserHealthInfo(id, info);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // -------------------- GET: Estimate daily/weekly calories --------------------
    @GetMapping("/estimate")
    public ResponseEntity<?> estimateCalories(
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "username", required = false) String username) {

        UserHealthInfo info = null;

        if (email != null && !email.isEmpty()) info = service.findByEmail(email);
        else if (username != null && !username.isEmpty()) {
            User u = userRepository.findByUsername(username);
            if (u != null) info = service.findByUserUID(u.getUID());
        }

        if (info == null) return ResponseEntity.notFound().build();

        // Compute age
        java.time.LocalDate dob = info.getDateOfBirth();
        int age = 30;
        if (dob != null) {
            age = java.time.Period.between(dob, java.time.LocalDate.now()).getYears();
        }

        double heightCm = info.getHeightFt() * 30.48 + info.getHeightIn() * 2.54;
        double weightKg = info.getWeight() * 0.453592;
        String sex = info.getSex() == null ? "male" : info.getSex().toLowerCase();

        double bmr;
        if (sex.startsWith("f")) bmr = 10 * weightKg + 6.25 * heightCm - 5 * age - 161;
        else bmr = 10 * weightKg + 6.25 * heightCm - 5 * age + 5;

        String activity = info.getWeeklyActivityLevel() == null ? "sedentary" : info.getWeeklyActivityLevel().toLowerCase();
        double factor = 1.2;
        if (activity.contains("light")) factor = 1.375;
        else if (activity.contains("moderate")) factor = 1.55;
        else if (activity.contains("active")) factor = 1.725;
        else if (activity.contains("very")) factor = 1.9;

        double daily = bmr * factor;
        double weekly = daily * 7;

        Map<String,Object> resp = new HashMap<>();
        resp.put("dailyCalorieGoal", Math.round(daily));
        resp.put("weeklyCalorieGoal", Math.round(weekly));
        resp.put("bmr", Math.round(bmr));
        return ResponseEntity.ok(resp);
    }
}
