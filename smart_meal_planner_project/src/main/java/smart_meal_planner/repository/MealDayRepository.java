package smart_meal_planner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import smart_meal_planner.model.MealDay;

@Repository
public interface MealDayRepository extends JpaRepository<MealDay, Long> {
}
