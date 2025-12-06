package smart_meal_planner.test;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import smart_meal_planner.controller.RecipeController;
import smart_meal_planner.model.MealPlan;
import smart_meal_planner.service.RecipeSearchRequest;
import smart_meal_planner.service.RecipeService;

import java.util.Arrays;

class RecipeControllerTest {

    @InjectMocks
    private RecipeController controller;

    @Mock
    private RecipeService recipeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ------------------ /recipes endpoint ------------------
    @Test
    void getRecipes_HappyPath() {
        RecipeSearchRequest request = new RecipeSearchRequest();
        request.setIngredients(Arrays.asList("chicken", "rice"));
        request.setMaxPrice(50.0);

        MealPlan mockPlan = new MealPlan();
        when(recipeService.findRecipeByString(request.getIngredients(), request.getMaxPrice()))
                .thenReturn(mockPlan);

        MealPlan result = controller.getRecipes(request);

        assertNotNull(result);
        assertEquals(mockPlan, result);

        verify(recipeService).findRecipeByString(request.getIngredients(), request.getMaxPrice());
    }

    @Test
    void getRecipes_FailurePath_ServiceReturnsNull() {
        RecipeSearchRequest request = new RecipeSearchRequest();
        request.setIngredients(Arrays.asList("beef"));
        request.setMaxPrice(30.0);

        when(recipeService.findRecipeByString(request.getIngredients(), request.getMaxPrice()))
                .thenReturn(null);

        MealPlan result = controller.getRecipes(request);

        assertNull(result);
        verify(recipeService).findRecipeByString(request.getIngredients(), request.getMaxPrice());
    }

    @Test
    void getRecipes_FailurePath_ServiceThrowsException() {
        RecipeSearchRequest request = new RecipeSearchRequest();
        request.setIngredients(Arrays.asList("fish"));
        request.setMaxPrice(20.0);

        when(recipeService.findRecipeByString(request.getIngredients(), request.getMaxPrice()))
                .thenThrow(new RuntimeException("Service error"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            controller.getRecipes(request);
        });

        assertEquals("Service error", ex.getMessage());
        verify(recipeService).findRecipeByString(request.getIngredients(), request.getMaxPrice());
    }
}
