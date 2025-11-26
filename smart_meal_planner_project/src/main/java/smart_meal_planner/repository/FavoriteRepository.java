package smart_meal_planner.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import smart_meal_planner.model.Favorite;
import smart_meal_planner.model.User;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUser(User user);
    Favorite findByUserAndRecipeId(User user, int recipeId);
}

