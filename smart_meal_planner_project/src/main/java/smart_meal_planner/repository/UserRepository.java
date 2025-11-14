package smart_meal_planner.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import smart_meal_planner.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>{
    User findByUsername(String username);
    User findByEmail (String email);  
}
