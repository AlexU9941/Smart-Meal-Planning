package smart_meal_planner.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import smart_meal_planner.model.IngredientInput;

@Repository
public interface IngredientRepository extends JpaRepository<IngredientInput, Long> {

    // Used when resetting all preferences for a user
    void deleteAllByUser_UID(Long userId);

    // Fetch only the current user's ingredients
    List<IngredientInput> findAllByUser_UID(Long userId);
}
