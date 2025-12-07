package smart_meal_planner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import smart_meal_planner.model.MealPlan;

import java.util.List;

@Repository
public interface MealPlanRepository extends JpaRepository<MealPlan, Long> {

    // 🔥 NEW — get all meal plans belonging to a user
    List<MealPlan> findByUserId(Long userId);

    // 🔥 NEW — get the latest saved meal plan for a user
    MealPlan findTopByUserIdOrderByIdDesc(Long userId);
}
