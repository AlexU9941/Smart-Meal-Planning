package smart_meal_planner.test;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import smart_meal_planner.controller.MealPlanController;
import smart_meal_planner.dto.MealPlanDTO;
import smart_meal_planner.dto.MealPlanResponseDTO;
import smart_meal_planner.model.MealPlan;
import smart_meal_planner.model.UserNutritionalGoals;
import smart_meal_planner.repository.MealPlanRepository;
import smart_meal_planner.service.GenerateRequest;
import smart_meal_planner.service.MealPlanService;
import smart_meal_planner.service.RecipeService;

class MealPlanControllerTest {

    @InjectMocks
    private MealPlanController controller;

    @Mock
    private MealPlanService mealPlanService;

    @Mock
    private RecipeService recipeService;

    @Mock
    private MealPlanRepository mealPlanRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createMealPlan_HappyPath() {
        MealPlanDTO dto = new MealPlanDTO();
        MealPlan mockPlan = new MealPlan();

        when(mealPlanService.saveMealPlan(dto)).thenReturn(mockPlan);

        MealPlan result = controller.createMealPlan(dto);

        assertEquals(mockPlan, result);
        verify(mealPlanService).saveMealPlan(dto);
    }

    @Test
    void getMealPlan_HappyPath() {
        Long id = 1L;
        MealPlan mockPlan = new MealPlan();
        when(mealPlanService.getMealPlan(id)).thenReturn(mockPlan);

        MealPlan result = controller.getMealPlan(id);

        assertEquals(mockPlan, result);
        verify(mealPlanService).getMealPlan(id);
    }

    @Test
    void getNutritionDiff_HappyPath() {
        Long id = 1L;
        UserNutritionalGoals goals = new UserNutritionalGoals();
        double[][] mockDiff = new double[][]{{1.0}};
        when(mealPlanService.compareNutrition(id, goals)).thenReturn(mockDiff);

        double[][] result = controller.getNutritionDiff(id, goals);

        assertArrayEquals(mockDiff, result);
        verify(mealPlanService).compareNutrition(id, goals);
    }

    @Test
    void generateMealPlan_HappyPath() throws Exception {
        GenerateRequest request = new GenerateRequest();
        request.setBudget(50.0);
        request.setIngredients(Arrays.asList("chicken"));

        MealPlan mockPlan = new MealPlan();
        MealPlanResponseDTO mockDto = new MealPlanResponseDTO();

        when(recipeService.findRecipeByString(anyList(), eq(50.0))).thenReturn(mockPlan);
        when(mealPlanService.toDTO(mockPlan)).thenReturn(mockDto);

        ResponseEntity<MealPlanResponseDTO> response = controller.generateMealPlan(request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockDto, response.getBody());

        verify(recipeService).findRecipeByString(Arrays.asList("chicken"), 50.0);
        verify(mealPlanService).toDTO(mockPlan);
    }

    @Test
    void generateMealPlan_FailurePath() throws Exception {
        GenerateRequest request = new GenerateRequest();
        request.setBudget(50.0);
        request.setIngredients(Collections.emptyList());

        when(recipeService.findRecipeByString(anyList(), anyDouble()))
                .thenThrow(new RuntimeException("API error"));

        ResponseEntity<MealPlanResponseDTO> response = controller.generateMealPlan(request);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody()); // returns empty DTO
    }
}
