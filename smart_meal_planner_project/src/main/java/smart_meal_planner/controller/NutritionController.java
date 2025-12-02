package smart_meal_planner.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import smart_meal_planner.model.MealPlan;
//import smart_meal_planner.model.Meal; not an actual class 
import smart_meal_planner.model.NutritionInfo;
import smart_meal_planner.model.UserGoals;
import smart_meal_planner.nutrition.Nutrition;
import smart_meal_planner.service.NutritionRequest;
import smart_meal_planner.service.NutritionService;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/nutrition")
@CrossOrigin(origins = "*")
public class NutritionController {

    @Autowired
    private NutritionService nutritionService;

    //@PostMapping("/track")
    // public NutritionInfo trackNutrition(@RequestBody List<Meal> mealPlan) {
    //     if (mealPlan == null || mealPlan.isEmpty()) {
    //         throw new IllegalArgumentException("No meal plan provided.");
    //     }
    //     return nutritionService.calculateMealPlanNutrition(mealPlan);
    // }
    
    @PostMapping("/track")
    public double[][] getNutritionDiff(@RequestBody NutritionRequest request) {
        return nutritionService.compareMealPlanToGoals(request.getMealPlan(), request.getUserGoals());
    }

    @PostMapping("/health-check")
    public String healthCheck(@RequestBody HealthCheckRequest request) {
        if (request.getWeeklyTotals() == null) {
            throw new IllegalArgumentException("Weekly totals missing.");
        }
        return nutritionService.evaluateHealthGoals(request.getWeeklyTotals(), request.getUserGoals());
    }

    // Accepts a simplified weekly plan from the frontend (array of day objects with lunch/dinner titles)
    @PostMapping("/summary")
    public SummaryResponse summarize(@RequestBody FrontendWeekPlan plan) {
        // Build NutritionInfo totals per meal using simple parsing; if nutrition provided by frontend, use it
        NutritionInfo weeklyTotals = new NutritionInfo();
        Map<String, NutritionInfo> perMeal = new HashMap<>();
        // Build days list for frontend-friendly response
        java.util.List<DayTotals> daysList = new java.util.ArrayList<>();

        if (plan == null || plan.days == null) {
            throw new IllegalArgumentException("Invalid plan payload");
        }

        for (FrontendDay d : plan.days) {
            if (d == null) continue;
            DayTotals dt = new DayTotals();
            dt.day = d.day;
            dt.totals = new NutritionInfo();
            dt.meals = new java.util.ArrayList<>();
            for (String key : new String[]{"lunch", "dinner"}) {
                FrontendMeal m = key.equals("lunch") ? d.lunch : d.dinner;
                if (m == null) continue;
                NutritionInfo n = new NutritionInfo();
                if (m.nutrition != null) {
                    // Attempt to coerce values
                    n.setCalories(safeDouble(m.nutrition.get("calories")));
                    n.setProtein(safeDouble(m.nutrition.get("protein")));
                    n.setCarbs(safeDouble(m.nutrition.get("carbs")));
                    n.setFat(safeDouble(m.nutrition.get("fat")));
                    n.setSugar(safeDouble(m.nutrition.get("addedSugars")));
                    n.setCholesterol(safeDouble(m.nutrition.get("cholesterol")));
                } else {
                    // fallback heuristic: derive small mock values based on title length
                    int seed = (m.title == null) ? 5 : m.title.length();
                    n.setCalories(200 + (seed * 10) % 400);
                    n.setProtein(10 + (seed * 3) % 40);
                    n.setCarbs(20 + (seed * 5) % 100);
                    n.setFat(5 + (seed * 2) % 50);
                    n.setSugar((seed * 2) % 15);
                    n.setCholesterol((seed * 4) % 100);
                }

                perMeal.put(m.title, n);
                // add to day totals and meals
                dt.meals.add(new MealWithNutrition(m.title, n));
                dt.totals.setCalories(dt.totals.getCalories() + n.getCalories());
                dt.totals.setProtein(dt.totals.getProtein() + n.getProtein());
                dt.totals.setCarbs(dt.totals.getCarbs() + n.getCarbs());
                dt.totals.setFat(dt.totals.getFat() + n.getFat());
                dt.totals.setSugar(dt.totals.getSugar() + n.getSugar());
                dt.totals.setCholesterol(dt.totals.getCholesterol() + n.getCholesterol());

                weeklyTotals.setCalories(weeklyTotals.getCalories() + n.getCalories());
                weeklyTotals.setProtein(weeklyTotals.getProtein() + n.getProtein());
                weeklyTotals.setCarbs(weeklyTotals.getCarbs() + n.getCarbs());
                weeklyTotals.setFat(weeklyTotals.getFat() + n.getFat());
                weeklyTotals.setSugar(weeklyTotals.getSugar() + n.getSugar());
                weeklyTotals.setCholesterol(weeklyTotals.getCholesterol() + n.getCholesterol());
            }
            daysList.add(dt);
        }

        SummaryResponse resp = new SummaryResponse();
        resp.weeklyTotals = weeklyTotals;
        resp.perMeal = perMeal;
        resp.caloriesPerMeal = new HashMap<>();
        for (Map.Entry<String, NutritionInfo> e : perMeal.entrySet()) {
            Map<String, Double> mini = new HashMap<>();
            mini.put("calories", e.getValue().getCalories());
            resp.caloriesPerMeal.put(e.getKey(), mini);
        }
        resp.days = daysList;
        return resp;
    }

    public static class DayTotals { public String day; public NutritionInfo totals; public java.util.List<MealWithNutrition> meals; }
    public static class MealWithNutrition { public String title; public NutritionInfo nutrition; public MealWithNutrition(String t, NutritionInfo n){ this.title = t; this.nutrition = n; } }

    // enhance SummaryResponse
    public static class SummaryResponse { public NutritionInfo weeklyTotals; public Map<String, NutritionInfo> perMeal; public Map<String, Map<String,Double>> caloriesPerMeal; public java.util.List<DayTotals> days; }

    private double safeDouble(Object o) {
        if (o == null) return 0.0;
        try {
            return Double.parseDouble(o.toString());
        } catch (Exception e) {
            return 0.0;
        }
    }

    // Inner class for request payload
    public static class HealthCheckRequest {
        private NutritionInfo weeklyTotals;
        private UserGoals userGoals;

        public NutritionInfo getWeeklyTotals() { return weeklyTotals; }
        public void setWeeklyTotals(NutritionInfo weeklyTotals) { this.weeklyTotals = weeklyTotals; }

        public UserGoals getUserGoals() { return userGoals; }
        public void setUserGoals(UserGoals userGoals) { this.userGoals = userGoals; }
    }

    // Simple DTOs for incoming plan
    public static class FrontendWeekPlan { public FrontendDay[] days; }
    public static class FrontendDay { public String day; public FrontendMeal lunch; public FrontendMeal dinner; }
    public static class FrontendMeal { public String title; public Map<String,Object> nutrition; }
}
