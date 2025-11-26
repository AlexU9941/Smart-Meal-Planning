//TEMPORARILY COMMENTED OUT DUE TO ERRORS
// package smart_meal_planner.controller;

// import org.springframework.web.bind.annotation.*;
// import smart_meal_planner.recipe.RecipeResult;
// import smart_meal_planner.service.RecipeFilterService;

// import java.util.List;

// @RestController
// @RequestMapping("/api/recipes")
// public class RecipeFilterController {

//     private final RecipeFilterService filterService;

//     public RecipeFilterController(RecipeFilterService filterService) {
//         this.filterService = filterService;
//     }

//     @PostMapping("/filter")
//     public List<RecipeResult> filterRecipes(@RequestBody FilterRequest request) {
//         List<RecipeResult> filtered = filterService.filterRecipes(
//                 request.getRecipes(),
//                 request.getDietaryRestriction(),
//                 request.getIngredients(),
//                 request.getMaxPrepTime(),
//                 request.getDifficulty(),
//                 request.getTimeOfDay(),
//                 request.getMaxCalories()
//         );

//         if (filtered.isEmpty()) {
//             throw new IllegalArgumentException("No recipes match the selected filters.");
//         }
//         return filtered;
//     }

//     public static class FilterRequest {
//         private List<RecipeResult> recipes;
//         private String dietaryRestriction;
//         private List<String> ingredients;
//         private Integer maxPrepTime;
//         private String difficulty;
//         private String timeOfDay;
//         private Double maxCalories;

//         // Getters and Setters
//     }
// }
