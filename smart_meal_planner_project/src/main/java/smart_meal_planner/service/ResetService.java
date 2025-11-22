package smart_meal_planner.service;

import smart_meal_planner.repository.UserRepository;
import smart_meal_planner.repository.UserGoalsRepository;
import smart_meal_planner.repository.UserIngredientsRepository;
import smart_meal_planner.repository.BudgetRepository;
import org.springframework.stereotype.Service;

@Service
public class ResetService {

    private final UserRepository userRepository;
    private final UserGoalsRepository userGoalsRepository;
    private final UserIngredientsRepository userIngredientsRepository;
    private final BudgetRepository budgetRepository;

    public ResetService(UserRepository userRepository,
                        UserGoalsRepository userGoalsRepository,
                        UserIngredientsRepository userIngredientsRepository,
                        BudgetRepository budgetRepository) {
        this.userRepository = userRepository;
        this.userGoalsRepository = userGoalsRepository;
        this.userIngredientsRepository = userIngredientsRepository;
        this.budgetRepository = budgetRepository;
    }

    public void resetUserPreferences(Long userId) {
        // Reset Health Goals
        userGoalsRepository.deleteByUserId(userId);

        // Reset Ingredients
        userIngredientsRepository.deleteAllByUserId(userId);

        // Reset Budget
        budgetRepository.deleteByUserId(userId);
    }
}
