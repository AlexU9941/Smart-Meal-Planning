package smart_meal_planner.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import smart_meal_planner.model.RecipeEntity;

public interface RecipeRepository extends JpaRepository<RecipeEntity, Long> {

    @Query("SELECT DISTINCT r FROM RecipeEntity r JOIN r.dishTypes dt " +
           "WHERE dt IN :dishTypes AND r.id <> :excludeId")
    List<RecipeEntity> findByDishTypes(@Param("dishTypes") List<String> dishTypes,
                                       @Param("excludeId") long excludeId);

    @Query("SELECT DISTINCT r FROM RecipeEntity r JOIN r.ingredients i " +
           "WHERE i.name IN :ingredientNames AND r.id <> :excludeId")
    List<RecipeEntity> findByIngredients(@Param("ingredientNames") List<String> ingredientNames,
                                         @Param("excludeId") long excludeId);
}
