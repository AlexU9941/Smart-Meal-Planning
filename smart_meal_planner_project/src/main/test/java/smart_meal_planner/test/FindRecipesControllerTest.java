package smart_meal_planner.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import smart_meal_planner.controller.FindRecipesController;
import smart_meal_planner.model.*;
import smart_meal_planner.recipe.RecipeResult;
import smart_meal_planner.repository.RecipeRepository;
import smart_meal_planner.service.RecipeService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FindRecipesControllerTest {

    private RecipeService recipeService;
    private RecipeRepository recipeRepository;
    private FindRecipesController controller;

    @BeforeEach
    void setUp() {
        recipeService = mock(RecipeService.class);
        recipeRepository = mock(RecipeRepository.class);
        controller = new FindRecipesController(recipeService, recipeRepository);
    }

    // ---------------------------------------------
    // searchRecipes() -- HAPPY PATH
    // ---------------------------------------------
    @Test
    void testSearchRecipes_ReturnsUniqueRecipes() {
        String search = "chicken";
        Double budget = 50.0;

        RecipeEntity lunch = new RecipeEntity();
        lunch.setId(1L);
        lunch.setTitle("Grilled Chicken");
        lunch.setPricePerServing(10.0);

        RecipeEntity dinner = new RecipeEntity();
        dinner.setId(2L);
        dinner.setTitle("Chicken Soup");
        dinner.setPricePerServing(12.0);

        MealDay day = new MealDay();
        day.setLunch(lunch);
        day.setDinner(dinner);

        MealPlan plan = new MealPlan();
        plan.setDays(Collections.singletonList(day));

        when(recipeService.findRecipeByString(Collections.singletonList(search), budget))
                .thenReturn(plan);

        List<FindRecipesController.FindRecipeDto> result = controller.searchRecipes(search, budget);

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(r -> r.getName().equals("Grilled Chicken")));
        assertTrue(result.stream().anyMatch(r -> r.getName().equals("Chicken Soup")));
        verify(recipeService).findRecipeByString(Collections.singletonList(search), budget);
    }

    // ---------------------------------------------
    // searchRecipes() -- UNHAPPY PATH: null mealPlan
    // ---------------------------------------------
    @Test
    void testSearchRecipes_NullMealPlan_ReturnsEmptyList() {
        when(recipeService.findRecipeByString(anyList(), anyDouble())).thenReturn(null);

        List<FindRecipesController.FindRecipeDto> result = controller.searchRecipes("anything", 100.0);
        assertTrue(result.isEmpty());
    }

    // ---------------------------------------------
    // allRecipes() -- HAPPY PATH
    // ---------------------------------------------
    @Test
    void testAllRecipes_ReturnsMappedDtos() {
        RecipeEntity r1 = new RecipeEntity();
        r1.setId(1L);
        r1.setTitle("Pasta");
        RecipeEntity r2 = new RecipeEntity();
        r2.setId(2L);
        r2.setTitle("Salad");

        when(recipeRepository.findAll()).thenReturn(Arrays.asList(r1, r2));

        List<FindRecipesController.SimpleRecipeDto> result = controller.allRecipes();
        assertEquals(2, result.size());
        assertEquals("Pasta", result.get(0).title);
        assertEquals("Salad", result.get(1).title);
        verify(recipeRepository).findAll();
    }

    // ---------------------------------------------
    // filterRecipes() -- HAPPY PATH: filters by maxPrepTime
    // ---------------------------------------------
    @Test
    void testFilterRecipes_ByMaxPrepTime() {
        RecipeEntity easy = new RecipeEntity();
        easy.setId(1L);
        easy.setTitle("Easy Dish");
        easy.setReadyInMinutes(20);

        RecipeEntity hard = new RecipeEntity();
        hard.setId(2L);
        hard.setTitle("Hard Dish");
        hard.setReadyInMinutes(90);

        when(recipeRepository.findAll()).thenReturn(Arrays.asList(easy, hard));

        List<FindRecipesController.SimpleRecipeDto> result =
                controller.filterRecipes(null, null, 30, null, null, null);

        assertEquals(1, result.size());
        assertEquals("Easy Dish", result.get(0).title);
    }

    // ---------------------------------------------
    // filterRecipes() -- UNHAPPY PATH: dietary filter no match
    // ---------------------------------------------
    @Test
    void testFilterRecipes_DietaryNoMatch() {
        RecipeEntity r1 = new RecipeEntity();
        r1.setId(1L);
        r1.setTitle("Dish");
        r1.setDishTypes(Arrays.asList("lunch"));

        when(recipeRepository.findAll()).thenReturn(Collections.singletonList(r1));

        List<FindRecipesController.SimpleRecipeDto> result =
                controller.filterRecipes("vegan", null, null, null, null, null);

        assertTrue(result.isEmpty());
    }

    // ---------------------------------------------
    // filterRecipes() -- UNHAPPY PATH: ingredient filter
    // ---------------------------------------------
    @Test
    void testFilterRecipes_IngredientFilterNoMatch() {
        RecipeEntity r1 = new RecipeEntity();
        r1.setId(1L);
        r1.setTitle("Dish");
        r1.setIngredients(Arrays.asList(new smart_meal_planner.model.Ingredient("tomato")));

        when(recipeRepository.findAll()).thenReturn(Collections.singletonList(r1));

        List<FindRecipesController.SimpleRecipeDto> result =
                controller.filterRecipes(null, "cheese", null, null, null, null);

        assertTrue(result.isEmpty());
    }
}
