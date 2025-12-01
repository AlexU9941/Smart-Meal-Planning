package smart_meal_planner.controller;

import smart_meal_planner.service.AlternativeMealService;
import smart_meal_planner.model.RecipeEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alternatives")
@CrossOrigin(origins = "*")
public class AlternativeMealController {

    private final AlternativeMealService service;

    public AlternativeMealController(AlternativeMealService service) {
        this.service = service;
    }

    @GetMapping("/{recipeId}")
    public List<RecipeEntity> getAlternatives(@PathVariable Long recipeId) {
        return service.getAlternativeMeals(recipeId);
    }
}
