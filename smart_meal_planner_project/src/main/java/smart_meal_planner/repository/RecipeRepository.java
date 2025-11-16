package smart_meal_planner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smart_meal_planner.recipe.RecipeResult;

public interface RecipeRepository extends JpaRepository<RecipeResult, Long> {
}
