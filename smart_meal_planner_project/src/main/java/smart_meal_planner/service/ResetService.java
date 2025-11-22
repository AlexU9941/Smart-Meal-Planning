package smart_meal_planner.service;

import smart_meal_planner.repository.UserRepository;
import smart_meal_planner.repository.UserNutritionalGoalsRepository;
import smart_meal_planner.repository.IngredientRepository;
import smart_meal_planner.repository.BudgetRepository;
import org.springframework.stereotype.Service;

@Service
public class ResetService {

    private final UserRepository userRepository;
    private final UserNutritionalGoalsRepository userGoalsRepository;
    private final IngredientRepository ingredientRepository;
    private final BudgetRepository budgetRepository;

    public ResetService(UserRepository userRepository,
                        UserNutritionalGoalsRepository userGoalsRepository,
                        IngredientRepository ingredientRepository,
                        BudgetRepository budgetRepository) {
        this.userRepository = userRepository;
        this.userGoalsRepository = userGoalsRepository;
        this.ingredientRepository = ingredientRepository;
        this.budgetRepository = budgetRepository;
    }

    public void resetUserPreferences(Long userId) {
        // Reset Health Goals
        userGoalsRepository.deleteByUser_UID(userId);

        // Reset Ingredients
        ingredientRepository.deleteAllByUser_UID(userId);

        // Reset Budget
        budgetRepository.deleteByUserId(userId);
    }
}
