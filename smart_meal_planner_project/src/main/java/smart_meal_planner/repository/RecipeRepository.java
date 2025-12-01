package smart_meal_planner.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import smart_meal_planner.model.RecipeEntity;

public interface RecipeRepository extends JpaRepository<RecipeEntity, Long> {

    // Find alternative meals by shared ingredients
    @Query(
        "SELECT DISTINCT r FROM RecipeEntity r " +
        "JOIN r.ingredients i " +
        "WHERE i.name IN :ingredientNames " +
        "AND r.id <> :id"
    )
    List<RecipeEntity> findBySharedIngredients(
            @Param("ingredientNames") List<String> ingredientNames,
            @Param("id") long id
    );
}
