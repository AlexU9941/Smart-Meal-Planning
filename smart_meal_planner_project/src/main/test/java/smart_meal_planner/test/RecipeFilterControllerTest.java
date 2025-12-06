package smart_meal_planner.test;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import smart_meal_planner.controller.RecipeFilterController;
import smart_meal_planner.recipe.RecipeResult;
import smart_meal_planner.service.RecipeFilterService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class RecipeFilterControllerTest {

    @InjectMocks
    private RecipeFilterController controller;

    @Mock
    private RecipeFilterService filterService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ------------------ /filter endpoint ------------------
    @Test
    void filterRecipes_HappyPath() {
        RecipeFilterController.FilterRequest request = new RecipeFilterController.FilterRequest();
        RecipeResult r1 = new RecipeResult();
        request.setRecipes(Arrays.asList(r1));

        when(filterService.filterRecipes(
                request.getRecipes(),
                request.getDietaryRestriction(),
                request.getIngredients(),
                request.getMaxPrepTime(),
                request.getDifficulty(),
                request.getTimeOfDay(),
                request.getMaxCalories()
        )).thenReturn(Arrays.asList(r1));

        List<RecipeResult> result = controller.filterRecipes(request);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(r1, result.get(0));

        verify(filterService).filterRecipes(
                request.getRecipes(),
                request.getDietaryRestriction(),
                request.getIngredients(),
                request.getMaxPrepTime(),
                request.getDifficulty(),
                request.getTimeOfDay(),
                request.getMaxCalories()
        );
    }

    @Test
    void filterRecipes_FailurePath_EmptyResult() {
        RecipeFilterController.FilterRequest request = new RecipeFilterController.FilterRequest();
        request.setRecipes(Collections.emptyList());

        when(filterService.filterRecipes(
                anyList(), anyString(), anyList(), any(), anyString(), anyString(), any()
        )).thenReturn(Collections.emptyList());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            controller.filterRecipes(request);
        });

        assertEquals("No recipes match the selected filters.", ex.getMessage());
        verify(filterService).filterRecipes(
                anyList(), anyString(), anyList(), any(), anyString(), anyString(), any()
        );
    }

    @Test
    void filterRecipes_FailurePath_ServiceThrowsException() {
        RecipeFilterController.FilterRequest request = new RecipeFilterController.FilterRequest();

        when(filterService.filterRecipes(
                anyList(), anyString(), anyList(), any(), anyString(), anyString(), any()
        )).thenThrow(new RuntimeException("Service error"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            controller.filterRecipes(request);
        });

        assertEquals("Service error", ex.getMessage());
        verify(filterService).filterRecipes(
                anyList(), anyString(), anyList(), any(), anyString(), anyString(), any()
        );
    }
}
