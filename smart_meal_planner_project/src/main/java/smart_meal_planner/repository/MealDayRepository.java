package smart_meal_planner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smart_meal_planner.model.MealDay;

public interface MealDayRepository extends JpaRepository<MealDay, Long> {
}
