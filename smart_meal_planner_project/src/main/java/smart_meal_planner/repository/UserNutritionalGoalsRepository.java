package smart_meal_planner.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import smart_meal_planner.model.UserNutritionalGoals;

@Repository
public interface UserNutritionalGoalsRepository extends JpaRepository<UserNutritionalGoals, Long> {
    //UserHealthInfo findByUID(Long uid);
    UserNutritionalGoals findByUser_UID(Long UID);
    UserNutritionalGoals findByEmail(String email); 

    //used during resetting preferences
    void deleteByUserId(Long UID);
}
