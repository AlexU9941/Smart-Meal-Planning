package smart_meal_planner.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import smart_meal_planner.model.UserHealthInfo;
import smart_meal_planner.service.UserHealthInfoService;

import java.util.Optional;

@RestController
@RequestMapping("/api/health-info")
@CrossOrigin(origins = "http://localhost:3000") // Allow React frontend to access backend
public class UserHealthInfoController {

    @Autowired
    private UserHealthInfoService service;

    @PostMapping
    public UserHealthInfo addHealthInfo(@RequestBody UserHealthInfo info) {
        return service.saveUserHealthInfo(info);
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
