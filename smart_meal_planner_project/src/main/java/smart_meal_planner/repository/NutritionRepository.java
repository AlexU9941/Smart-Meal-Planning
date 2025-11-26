package smart_meal_planner.repository;
import smart_meal_planner.model.Meal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NutritionRepository extends JpaRepository<Meal, Long> {
}