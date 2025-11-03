package smart_meal_planner.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import smart_meal_planner.model.Ingredient;
import smart_meal_planner.service.IngredientService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ingredients")
@CrossOrigin(origins = "http://localhost:3000") // allows React to connect
public class IngredientController {

    @Autowired
    private IngredientService service;

    @PostMapping
    public List<Ingredient> addIngredients(@RequestBody Map<String, List<Ingredient>> payload) {
        List<Ingredient> ingredients = payload.get("ingredients");
        if (ingredients == null) 
            {
            ingredients = new ArrayList<>(); // ensure non-null
            }
        return service.saveIngredients(ingredients);
    }


    @GetMapping
    public List<Ingredient> getAllIngredients() {
        return service.getAllIngredients();
    }
}

