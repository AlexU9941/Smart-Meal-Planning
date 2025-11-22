package smart_meal_planner.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;

import smart_meal_planner.service.ResetService;

//will reset Ingredients, Budget, Health Goals 
@RestController
@RequestMapping("/reset")
public class ResetController {

    private final ResetService resetService;

    public ResetController(ResetService resetService) {
        this.resetService = resetService;
    }

    @PostMapping("/{userId}/reset")
    public void resetUserPreferences(@PathVariable Long userId) {
        resetService.resetUserPreferences(userId);
    }
}
