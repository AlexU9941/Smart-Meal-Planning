package smart_meal_planner;

import java.util.Arrays;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.context.ApplicationContext; 

import smart_meal_planner.controller.RecipeController;
import smart_meal_planner.model.MealPlan;
import smart_meal_planner.service.RecipeService;

@SpringBootApplication
public class SmartMealPlannerApplication {
    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(SmartMealPlannerApplication.class, args);
        //SpringApplication.run(SmartMealPlannerApplication.class, args);
        System.out.println("Spring Boot app running...");

        // RecipeService recipeService = ctx.getBean(RecipeService.class);

        // MealPlan plan = recipeService.findRecipeByIngredients(
        //     Arrays.asList("chicken", "rice", "broccoli"),
        //     //Arrays.asList("chicken"),
        //     1000.0
        // );

        //System.out.println(plan.printMealPlan());


    }
}