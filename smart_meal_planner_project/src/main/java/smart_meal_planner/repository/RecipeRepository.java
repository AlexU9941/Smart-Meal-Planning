package smart_meal_planner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smart_meal_planner.model.RecipeEntity;

public interface RecipeRepository extends JpaRepository<RecipeEntity, Long> {
    
}
