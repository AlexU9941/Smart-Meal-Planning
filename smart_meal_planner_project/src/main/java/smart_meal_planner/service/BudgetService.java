package smart_meal_planner.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import smart_meal_planner.model.Budget;
import smart_meal_planner.repository.BudgetRepository;

@Service
public class BudgetService {

    @Autowired
    private BudgetRepository repository;

    public Budget saveBudget(Budget budget) {
        return repository.save(budget);
    }

    public Budget getBudgetByUserId(Long userId) {
        return repository.findByUserId(userId);
    }
}
