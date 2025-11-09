package smart_meal_planner.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import smart_meal_planner.model.User;
import smart_meal_planner.model.UserNutritionalGoals;
import java.util.Optional;

import smart_meal_planner.repository.UserRepository;
import smart_meal_planner.service.UserNutrtionalGoalsService;


@RestController
@RequestMapping("/api/nutritional-goals")
@CrossOrigin(origins = "http://localhost:3000")
public class UserNutritionalGoalsController {
    @Autowired 
    private UserRepository userRepository; 

    @Autowired
    private UserNutrtionalGoalsService service; 


    @PostMapping
    public ResponseEntity<?> addNutritionalGoals(@RequestBody UserNutritionalGoals goals)
    {
        User user = userRepository.findByEmail(goals.getEmail());

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found for email: " + goals.getEmail());
        }

        goals.setUser(user);

        service.saveUserNutritionalGoals(goals);

        return ResponseEntity.ok("Nutrtional goals saved successfully");

    }

    @GetMapping("/{id}")
    public Optional<UserNutritionalGoals> getNutrtionalGoals(@PathVariable Long id) {
        return service.getUserNutritionalGoals(id);
    }

    @PutMapping("/{id}")
    public UserNutritionalGoals updateNutrtionalGoals(@PathVariable Long id, @RequestBody UserNutritionalGoals goals) {
        return service.updateUserNutritionalGoals(id, goals);
    }
}
