package smart_meal_planner.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import smart_meal_planner.model.UserCreatedRecipe;
import smart_meal_planner.repository.UserCreatedRecipeRepository;

@Service
public class UserCreatedRecipeService {

    @Autowired
    private UserCreatedRecipeRepository repo;

    public UserCreatedRecipe saveUserCreatedRecipe(UserCreatedRecipe recipe) {
        return repo.save(recipe);
    }

    public List<UserCreatedRecipe> getUserCreatedRecipesByUserId(Long userId) {
        return repo.findByUserId(userId);
    }

}