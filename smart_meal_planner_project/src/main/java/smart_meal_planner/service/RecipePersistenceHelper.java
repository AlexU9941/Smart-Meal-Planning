package smart_meal_planner.service;

import org.springframework.stereotype.Component;
import smart_meal_planner.model.RecipeEntity;
import smart_meal_planner.recipe.RecipeResult;
import smart_meal_planner.repository.RecipeRepository;

@Component
public class RecipePersistenceHelper {

    private final RecipeRepository repo;

    public RecipePersistenceHelper(RecipeRepository repo) {
        this.repo = repo;
    }

    public RecipeEntity saveOrGetRecipe(RecipeResult recipeResult) {
        return repo.findById(recipeResult.getId())
                .orElseGet(() -> {
                    RecipeEntity entity = RecipeMapper.toEntity(recipeResult);
                    return repo.save(entity);
                });
    }
}
