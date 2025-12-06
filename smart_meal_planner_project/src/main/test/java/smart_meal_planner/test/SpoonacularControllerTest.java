package smart_meal_planner.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import smart_meal_planner.controller.SpoonacularController;
import smart_meal_planner.service.SpoonacularService;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SpoonacularControllerTest {

    @InjectMocks
    private SpoonacularController controller;

    @Mock
    private SpoonacularService spoonacularService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void test_ReturnsRandomRecipe() {
        Map<String, Object> mockRecipe = new HashMap<>();
        mockRecipe.put("id", 123);
        mockRecipe.put("title", "Mock Recipe");

        when(spoonacularService.getRandomRecipe()).thenReturn(mockRecipe);

        Map<String, Object> result = controller.test();

        assertNotNull(result);
        assertEquals(123, result.get("id"));
        assertEquals("Mock Recipe", result.get("title"));

        verify(spoonacularService, times(1)).getRandomRecipe();
    }
}
