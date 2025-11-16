package smart_meal_planner.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Optional;

import smart_meal_planner.model.UserNutritionalGoals;
import smart_meal_planner.repository.UserNutritionalGoalsRepository;

@Service
public class UserNutritionalGoalsService {

    @Autowired
    private UserNutritionalGoalsRepository repository;

     public UserNutritionalGoals saveUserNutritionalGoals(UserNutritionalGoals goals) {
        return repository.save(goals);
    }

    public Optional<UserNutritionalGoals> getUserNutritionalGoals(Long uid) {
        return repository.findById(uid);
    }

    public UserNutritionalGoals updateUserNutritionalGoals(Long uid, UserNutritionalGoals newGoals) {
        return repository.findById(uid).map(existing -> {
            existing.setDailyCaloriesGoal(newGoals.getDailyCaloriesGoal());
            existing.setDailyProteinGoal(newGoals.getDailyProteinGoal());
            existing.setDailyFatGoal(newGoals.getDailyFatGoal());
            existing.setDailyCarbohydratesGoal(newGoals.getDailyCarbohydratesGoal());
            existing.setDailySaturatedFatGoal(newGoals.getDailySaturatedFatGoal());
            existing.setDailySugarGoal(newGoals.getDailySugarGoal());
            existing.setDailyCholesterolGoal(newGoals.getDailyCholesterolGoal());
            existing.setDailySodiumGoal(newGoals.getDailySodiumGoal());
            return repository.save(existing);
        }).orElseThrow(() -> new RuntimeException("User not found with id: " + uid));
    }

}
