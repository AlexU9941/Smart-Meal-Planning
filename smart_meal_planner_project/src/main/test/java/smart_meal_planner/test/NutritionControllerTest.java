package smart_meal_planner.test;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import smart_meal_planner.controller.NutritionController;
import smart_meal_planner.model.NutritionInfo;
import smart_meal_planner.model.UserGoals;
import smart_meal_planner.service.NutritionRequest;
import smart_meal_planner.service.NutritionService;

class NutritionControllerTest {

    @InjectMocks
    private NutritionController controller;

    @Mock
    private NutritionService nutritionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ------------------ /track ------------------
    @Test
    void track_HappyPath() {
        NutritionRequest request = new NutritionRequest();
        request.setMealPlan(null); // can be null for mock
        request.setUserGoals(new UserGoals());

        double[][] mockDiff = {{1.0, 2.0}, {3.0, 4.0}};
        when(nutritionService.compareMealPlanToGoals(any(), any())).thenReturn(mockDiff);

        double[][] result = controller.getNutritionDiff(request);

        assertArrayEquals(mockDiff, result);
        verify(nutritionService).compareMealPlanToGoals(request.getMealPlan(), request.getUserGoals());
    }

    // ------------------ /health-check ------------------
    @Test
    void healthCheck_HappyPath() {
        NutritionController.HealthCheckRequest request = new NutritionController.HealthCheckRequest();
        NutritionInfo totals = new NutritionInfo();
        totals.setCalories(200);
        request.setWeeklyTotals(totals);
        request.setUserGoals(new UserGoals());

        when(nutritionService.evaluateHealthGoals(any(), any())).thenReturn("All goals met");

        String result = controller.healthCheck(request);
        assertEquals("All goals met", result);

        verify(nutritionService).evaluateHealthGoals(totals, request.getUserGoals());
    }

    @Test
    void healthCheck_FailurePath_MissingTotals() {
        NutritionController.HealthCheckRequest request = new NutritionController.HealthCheckRequest();
        request.setWeeklyTotals(null); // Missing totals

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            controller.healthCheck(request);
        });

        assertEquals("Weekly totals missing.", ex.getMessage());
    }

    // ------------------ /summary ------------------
    @Test
    void summary_HappyPath() {
        NutritionController.FrontendWeekPlan plan = new NutritionController.FrontendWeekPlan();
        NutritionController.FrontendDay day = new NutritionController.FrontendDay();
        day.day = "Monday";
        NutritionController.FrontendMeal lunch = new NutritionController.FrontendMeal();
        lunch.title = "Chicken Salad";
        lunch.nutrition = new HashMap<>();
        lunch.nutrition.put("calories", 300);
        day.lunch = lunch;
        day.dinner = null;
        plan.days = new NutritionController.FrontendDay[]{day};

        NutritionController.SummaryResponse resp = controller.summarize(plan);

        assertNotNull(resp);
        assertEquals(1, resp.days.size());
        assertEquals("Monday", resp.days.get(0).day);
        assertEquals(300, resp.days.get(0).totals.getCalories());
        assertTrue(resp.perMeal.containsKey("Chicken Salad"));
    }

    @Test
    void summary_FailurePath_NullPlan() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            controller.summarize(null);
        });

        assertEquals("Invalid plan payload", ex.getMessage());
    }
}
