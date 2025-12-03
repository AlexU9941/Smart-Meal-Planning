package smart_meal_planner.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import smart_meal_planner.model.Budget;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    Budget findByUserId(Long userId);

    void deleteByUserId(Long userId);
}
