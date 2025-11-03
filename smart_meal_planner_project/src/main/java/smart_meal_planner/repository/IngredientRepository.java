package smart_meal_planner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smart_meal_planner.model.IngredientInput;

public interface IngredientRepository extends JpaRepository<IngredientInput, Long> {
}
