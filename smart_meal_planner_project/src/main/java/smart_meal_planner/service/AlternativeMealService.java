package smart_meal_planner.service;

import org.springframework.stereotype.Service;
import smart_meal_planner.repository.RecipeRepository;
import smart_meal_planner.model.RecipeEntity;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AlternativeMealService {

    private final RecipeRepository recipeRepository;

    public AlternativeMealService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    public List<RecipeEntity> getAlternativeMeals(Long recipeId) {
        RecipeEntity current = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Recipe not found"));

        
        List<RecipeEntity> byCategory = new ArrayList<>();


        // 2. Shared ingredients alternatives
        List<String> ingredientNames = current.getIngredients().stream()
                .map(i -> i.getName().toLowerCase())
                .collect(Collectors.toList());

        List<RecipeEntity> byIngredients = recipeRepository.findBySharedIngredients(
                ingredientNames, recipeId
        );

        // Combine & remove duplicates
        Set<RecipeEntity> combined = new LinkedHashSet<>();
        combined.addAll(byCategory);
        combined.addAll(byIngredients);

        return new ArrayList<>(combined);
    }
}
