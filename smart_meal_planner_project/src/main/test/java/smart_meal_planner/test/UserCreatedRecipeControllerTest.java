package smart_meal_planner.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import smart_meal_planner.controller.UserCreatedRecipeController;
import smart_meal_planner.model.UserCreatedRecipe;
import smart_meal_planner.service.UserCreatedRecipeService;

import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserCreatedRecipeControllerTest {

    @InjectMocks
    private UserCreatedRecipeController controller;

    @Mock
    private UserCreatedRecipeService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addUserCreatedRecipe_ShouldReturnSavedRecipe() {
        UserCreatedRecipe recipe = new UserCreatedRecipe();
        recipe.setTitle("Test Recipe");

        UserCreatedRecipe savedRecipe = new UserCreatedRecipe();
        savedRecipe.setTitle("Test Recipe");

        when(service.saveUserCreatedRecipe(recipe)).thenReturn(savedRecipe);

        ResponseEntity<UserCreatedRecipe> response = controller.addUserCreatedRecipe(recipe);

        assertNotNull(response);
        assertEquals(savedRecipe, response.getBody());

        verify(service, times(1)).saveUserCreatedRecipe(recipe);
    }

    @Test
    void getUserCreatedRecipes_ShouldReturnList() {
        Long userId = 1L;
        UserCreatedRecipe r1 = new UserCreatedRecipe();
        r1.setTitle("Recipe 1");
        UserCreatedRecipe r2 = new UserCreatedRecipe();
        r2.setTitle("Recipe 2");

        List<UserCreatedRecipe> recipes = Arrays.asList(r1, r2);

        when(service.getUserCreatedRecipesByUserId(userId)).thenReturn(recipes);

        List<UserCreatedRecipe> result = controller.getUserCreatedRecipes(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Recipe 1", result.get(0).getTitle());

        verify(service, times(1)).getUserCreatedRecipesByUserId(userId);
    }
}
