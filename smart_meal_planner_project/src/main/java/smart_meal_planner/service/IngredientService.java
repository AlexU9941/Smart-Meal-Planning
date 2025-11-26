package smart_meal_planner.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import smart_meal_planner.model.IngredientInput;
import smart_meal_planner.repository.IngredientRepository;

@Service
public class IngredientService {

    @Autowired
    private IngredientRepository repo;

    public List<IngredientInput> saveIngredients(List<IngredientInput> ingredients) {
        return repo.saveAll(ingredients);
    }

    public List<IngredientInput> getAllIngredients() {
        return repo.findAll();
    }
}
