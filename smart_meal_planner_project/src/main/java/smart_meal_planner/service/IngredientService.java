package smart_meal_planner.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import smart_meal_planner.model.IngredientInput;
import smart_meal_planner.repository.IngredientRepository;

@Service
public class IngredientService {

    @Autowired
    private IngredientRepository repo;

    public List<IngredientInput> saveIngredients(List<IngredientInput> ingredients) {
        return repo.saveAll(ingredients);
    }

    public List<IngredientInput> getIngredientsForUser(Long userId) {
        return repo.findAllByUser_UID(userId);
    }

    /**
     * Returns true if the ingredient was deleted for this user, false otherwise.
     */
    public boolean deleteIngredientForUser(Long id, Long userId) {
        Optional<IngredientInput> opt = repo.findById(id);
        if (!opt.isPresent()) {
            return false;
        }

        IngredientInput ing = opt.get();
        if (ing.getUser() == null || ing.getUser().getUID() != userId) {
            return false;
        }

        repo.delete(ing);
        return true;
    }
}
