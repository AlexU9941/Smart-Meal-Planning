package smart_meal_planner.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import smart_meal_planner.model.User;
import smart_meal_planner.model.UserHealthInfo;
import smart_meal_planner.service.UserHealthInfoService;

import java.util.Optional;

import smart_meal_planner.repository.UserRepository;

@RestController
@RequestMapping("/api/health-info")
@CrossOrigin(origins = "http://localhost:3000") // Allow React frontend to access backend
public class UserHealthInfoController {

    @Autowired
    private UserHealthInfoService service;

    @Autowired
    private UserRepository userRepository; 

    // @PostMapping
    // public ResponseEntity<?>  addHealthInfo(@RequestBody UserHealthInfo info) {
    //     // return service.saveUserHealthInfo(info);
    //     try {
    //         service.saveUserHealthInfo(info);
    //         return ResponseEntity.ok("Saved successfully");
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //         return ResponseEntity.status(500).body("Database not connected");
    //     }
    // }

    @PostMapping("/health-info")
    public ResponseEntity<?> addHealthInfo(@RequestBody UserHealthInfo dto) {
        User user = userRepository.findByEmail(dto.getEmail());
        if (user == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found");

        UserHealthInfo info = new UserHealthInfo();
        info.setUser(user);
        info.setHeightFt(dto.getHeightFt());
        info.setHeightIn(dto.getHeightIn());
        info.setWeight(dto.getWeight());
        info.setSex(dto.getSex());
        info.setWeeklyActivityLevel(dto.getWeeklyActivityLevel());
        info.setAllergies(dto.getAllergies());

        service.saveUserHealthInfo(info);
        return ResponseEntity.ok("Health info saved");
    }


    @GetMapping("/{id}")
    public Optional<UserHealthInfo> getHealthInfo(@PathVariable Long id) {
        return service.getUserHealthInfoById(id);
    }

    @PutMapping("/{id}")
    public UserHealthInfo updateHealthInfo(@PathVariable Long id, @RequestBody UserHealthInfo info) {
        return service.updateUserHealthInfo(id, info);
    }
}
