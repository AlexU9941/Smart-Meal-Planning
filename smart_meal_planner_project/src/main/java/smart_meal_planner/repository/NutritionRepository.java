package smart_meal_planner.repository;

import.repository.JpaRepository;
import smart_meal_planner.model.Meal;

public interface MealRepository extends JpaRepository<Meal, Long> {
}