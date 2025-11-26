package smart_meal_planner.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import smart_meal_planner.model.UserCreatedRecipe;


@Repository
public interface UserCreatedRecipeRepository extends JpaRepository<UserCreatedRecipe, Long>{
    List<UserCreatedRecipe> findByUserId(Long userId);
}