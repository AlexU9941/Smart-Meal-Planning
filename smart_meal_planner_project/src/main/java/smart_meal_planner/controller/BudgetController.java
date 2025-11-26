package smart_meal_planner.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import smart_meal_planner.model.Budget;
import smart_meal_planner.service.BudgetService;

@RestController
@RequestMapping("/api/budget")
@CrossOrigin(origins = "http://localhost:3000")
public class BudgetController {

    @Autowired
    private BudgetService service;

    @PostMapping
    public Budget setBudget(@RequestBody Budget budget) {
        return service.saveBudget(budget);
    }

    @GetMapping("/{userId}")
    public Budget getBudget(@PathVariable Long userId) {
        return service.getBudgetByUserId(userId);
    }
}
