package smart_meal_planner.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import smart_meal_planner.model.GroceryAccount;
import smart_meal_planner.repository.GroceryAccountRepository;

@RestController
@RequestMapping("/api/groceryAccounts")
public class GroceryAccountController {
    @Autowired
    private GroceryAccountRepository repo;

    @GetMapping
    public List<GroceryAccount> getAll() { return repo.findAll(); }

    @PostMapping
    public GroceryAccount create(@RequestBody GroceryAccount account) { return repo.save(account); }

    @PutMapping("/{id}")
    public GroceryAccount update(@PathVariable Long id, @RequestBody GroceryAccount account) {
        account.setId(id);
        return repo.save(account);
    }
}
