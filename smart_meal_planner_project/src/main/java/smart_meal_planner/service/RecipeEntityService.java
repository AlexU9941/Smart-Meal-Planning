package smart_meal_planner.service;

import org.springframework.stereotype.Service;
import smart_meal_planner.model.RecipeEntity;
import smart_meal_planner.recipe.RecipeResult;
import smart_meal_planner.repository.RecipeRepository;

@Service
public class RecipeEntityService {

    private final RecipeRepository repo;

    public RecipeEntityService(RecipeRepository repo) {
        this.repo = repo;
    }

    public RecipeEntity saveFromApi(RecipeResult apiRecipe) {
        RecipeEntity entity = new RecipeEntity(
                apiRecipe.getId(),
                apiRecipe.getTitle(),
                apiRecipe.getImage(),
                apiRecipe.getReadyInMinutes(),
                apiRecipe.getServings(),
              //  apiRecipe.getSummary()
        );
        return repo.save(entity);
    }

    public RecipeEntity get(Long id) {
        return repo.findById(id).orElse(null);
    }
}
