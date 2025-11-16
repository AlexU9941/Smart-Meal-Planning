package smart_meal_planner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smart_meal_planner.model.GroceryAccount;

public interface GroceryAccountRepository extends JpaRepository<GroceryAccount, String> {
}
