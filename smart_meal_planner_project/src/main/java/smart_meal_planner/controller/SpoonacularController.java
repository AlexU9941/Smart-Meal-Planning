package smart_meal_planner.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import smart_meal_planner.service.SpoonacularService;

import java.util.Map;

@RestController
@RequestMapping("/api/spoonacular")
@CrossOrigin(origins = "http://localhost:3000")
public class SpoonacularController {

    @Autowired
    private SpoonacularService spoonacularService;

    @GetMapping("/test")
    public Map<String, Object> test() {
        return spoonacularService.getRandomRecipe();
    }
}
