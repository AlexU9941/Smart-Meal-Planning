package smart_meal_planner.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import smart_meal_planner.model.IngredientInput;
import smart_meal_planner.model.MealDay;
import smart_meal_planner.model.MealPlan;
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

    public List<String> extractIngredients(MealPlan plan) {
        List<String> ingredients = new ArrayList<>();

        for (MealDay day : plan.getDays()) {

            if (day.getLunch() != null) {
                day.getLunch().getIngredients()
                    .forEach(i -> ingredients.add(i.getName()));
            }

            if (day.getDinner() != null) {
                day.getDinner().getIngredients()
                    .forEach(i -> ingredients.add(i.getName()));
            }
        }

        return ingredients;
    }

    public List<String> uniqueIngredients(MealPlan plan) {
        return extractIngredients(plan).stream()
            .map(String::toLowerCase)
            .distinct()
            .toList();
    }
}
