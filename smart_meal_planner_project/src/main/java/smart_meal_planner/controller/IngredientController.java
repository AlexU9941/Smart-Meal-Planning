package smart_meal_planner.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import smart_meal_planner.model.IngredientInput;
import smart_meal_planner.service.IngredientService;

import java.util.List;

@RestController
@RequestMapping("/api/ingredients")
@CrossOrigin(origins = "http://localhost:3000")
public class IngredientController {

    @Autowired
    private IngredientService service;

    
    @PostMapping
    public List<IngredientInput> addIngredients(@RequestBody List<IngredientInput> ingredients) {
        return service.saveIngredients(ingredients);
    }

    @GetMapping
    public List<IngredientInput> getAllIngredients() {
        return service.getAllIngredients();
    }
}
