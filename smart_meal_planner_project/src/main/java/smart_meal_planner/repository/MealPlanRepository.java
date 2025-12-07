package smart_meal_planner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smart_meal_planner.model.MealPlan;

public interface MealPlanRepository extends JpaRepository<MealPlan, Long> {

    MealPlan findTopByUserIdOrderByIdDesc(Long userId);
}
