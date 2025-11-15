package smart_meal_planner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smart_meal_planner.model.Budget;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    // Optional: find budget by user ID
    Budget findByUserId(Long userId);
}
