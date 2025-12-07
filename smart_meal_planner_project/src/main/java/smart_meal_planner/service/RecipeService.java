package smart_meal_planner.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import smart_meal_planner.dto.Ingredient;
import smart_meal_planner.dto.RandomRecipeResponse;
import smart_meal_planner.model.MealDay;
import smart_meal_planner.model.MealPlan;
import smart_meal_planner.model.RecipeEntity;
import smart_meal_planner.model.IngredientInput;
import smart_meal_planner.recipe.RecipeResult;
import smart_meal_planner.recipe.RecipeSearchResponse;
import smart_meal_planner.repository.MealDayRepository;
import smart_meal_planner.repository.MealPlanRepository;
import smart_meal_planner.repository.RecipeRepository;

@Service
public class RecipeService {

    private final WebClient webClient;
    private final RecipeRepository recipeRepository;
    private final MealPlanRepository mealPlanRepository;
    private final MealDayRepository mealDayRepository;

    @Value("${spoonacular.api.key}")
    private String apiKey;

    public RecipeService(
            @Qualifier("spoonacularWebClient") WebClient spoonacularWebClient,
            RecipeRepository recipeRepository,
            MealPlanRepository mealPlanRepository,
            MealDayRepository mealDayRepository) {
        this.webClient = spoonacularWebClient;
        this.recipeRepository = recipeRepository;
        this.mealPlanRepository = mealPlanRepository;
        this.mealDayRepository = mealDayRepository;
    }

    @Transactional
    public MealPlan findRecipeByString(List<String> ingredients, Double maxPrice) {

        boolean hasIngredients = ingredients != null && !ingredients.isEmpty();
        boolean hasBudget = maxPrice != null && maxPrice.doubleValue() > 0;

        System.out.println("findRecipeByString -> ingredients=" + ingredients + ", maxPrice=" + maxPrice);
        List<RecipeResult> all = new ArrayList<RecipeResult>();

        try {
            if (!hasIngredients && !hasBudget) {
                all = fetchRandomRecipes(50);
            } else if (hasIngredients && !hasBudget) {
                all = fetchRecipesForIngredients(ingredients, 20);
            } else if (!hasIngredients && hasBudget) {
                List<RecipeResult> random = fetchRandomRecipes(50);
                all = random.stream()
                        .filter(r -> r.getPricePerServing() > 0)
                        .filter(r -> r.getPricePerServing() <= maxPrice.doubleValue())
                        .collect(Collectors.toList());
            } else {
                List<RecipeResult> tmp = fetchRecipesForIngredients(ingredients, 20);
                all = tmp.stream()
                        .filter(r -> r.getPricePerServing() > 0)
                        .filter(r -> r.getPricePerServing() <= maxPrice.doubleValue())
                        .collect(Collectors.toList());
            }

            if (all == null || all.isEmpty()) {
                System.out.println("No recipes found for the given criteria.");
                return new MealPlan();
            }

            Map<Long, RecipeResult> uniqueMap = all.stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toMap(
                            RecipeResult::getId,
                            r -> r,
                            (r1, r2) -> r1
                    ));

            List<RecipeResult> uniqueList = new ArrayList<RecipeResult>(uniqueMap.values());

            List<RecipeResult> ordered = hasIngredients
                    ? scoreRecipes(uniqueList, ingredients)
                    : uniqueList;

            int days = 7;
            int needed = days * 3;
            if (ordered.size() < needed) {
                System.out.println("Not enough recipes after filtering. Needed " + needed + ", got " + ordered.size());
                return new MealPlan();
            }

            List<RecipeResult> top = ordered.subList(0, needed);

            return assignMealsAndPersist(top);

        } catch (WebClientResponseException e) {
            System.out.println("API error: " + e.getStatusCode() + " - " + e.getMessage());
            return new MealPlan();
        } catch (Exception e) {
            e.printStackTrace();
            return new MealPlan();
        }
    }

    @Transactional
    public MealPlan findRecipeByIngredients(List<Ingredient> ingredients, Double maxPrice) {
        List<String> names = null;

        if (ingredients != null && !ingredients.isEmpty()) {
            names = ingredients.stream()
                    .filter(Objects::nonNull)
                    .map(Ingredient::getName)
                    .collect(Collectors.toList());
        }

        return findRecipeByString(names, maxPrice);
    }

    private List<RecipeResult> fetchRandomRecipes(int count) {
        RandomRecipeResponse response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/recipes/random")
                        .queryParam("number", count)
                        .queryParam("apiKey", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(RandomRecipeResponse.class)
                .block();

        if (response == null || response.getRecipes() == null) {
            return new ArrayList<RecipeResult>();
        }
        return response.getRecipes();
    }

    private List<RecipeResult> fetchRecipesForIngredients(List<String> ingredients, int numberPerIngredient) {
        List<RecipeResult> combined = new ArrayList<RecipeResult>();

        if (ingredients == null) {
            return combined;
        }

        for (String ing : ingredients) {
            if (ing == null || ing.trim().isEmpty()) {
                continue;
            }

            RecipeSearchResponse response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/recipes/complexSearch")
                            .queryParam("query", ing)
                            .queryParam("number", numberPerIngredient)
                            .queryParam("addRecipeInformation", true)
                            .queryParam("includeNutrition", true)
                            .queryParam("apiKey", apiKey)
                            .build())
                    .retrieve()
                    .bodyToMono(RecipeSearchResponse.class)
                    .block();

            if (response != null && response.getResults() != null) {
                combined.addAll(response.getResults());
            }
        }

        return combined;
    }

    private List<RecipeResult> scoreRecipes(List<RecipeResult> results, List<String> scoringIngredients) {
        for (RecipeResult recipe : results) {
            int score = 0;
            score += scoreByIngredient(recipe, scoringIngredients);
            recipe.setScore(score);
        }

        return results.stream()
                .sorted(Comparator.comparingInt(RecipeResult::getScore).reversed())
                .distinct()
                .collect(Collectors.toList());
    }

    private int scoreByIngredient(RecipeResult recipe, List<String> scoringIngredients) {
        List<Ingredient> ingredients = recipe.getExtendedIngredients();
        if (ingredients == null || scoringIngredients == null || scoringIngredients.isEmpty()) {
            return 0;
        }

        int score = 0;
        for (Ingredient ingredient : ingredients) {
            String name = ingredient.getName().toLowerCase();
            boolean matches = scoringIngredients.stream()
                    .anyMatch(i -> name.contains(i.toLowerCase()));
            if (matches) {
                score += 5;
            }
        }
        return score;
    }

    @Transactional
    private MealPlan assignMealsAndPersist(List<RecipeResult> sorted) {
        int days = 7;
        int needed = days * 3;

        if (sorted.size() < needed) {
            System.out.println("assignMealsAndPersist: not enough recipes, expected " + needed + " got " + sorted.size());
            return new MealPlan();
        }

        List<RecipeResult> breakfastResults = sorted.subList(0, days);
        List<RecipeResult> lunchResults = sorted.subList(days, 2 * days);
        List<RecipeResult> dinnerResults = sorted.subList(2 * days, 3 * days);

        MealPlan mealPlan = new MealPlan();
        List<MealDay> daysList = new ArrayList<MealDay>();

        List<RecipeEntity> breakfastEntities = breakfastResults.stream()
                .map(this::saveOrGetRecipeEntity)
                .collect(Collectors.toList());

        List<RecipeEntity> lunchEntities = lunchResults.stream()
                .map(this::saveOrGetRecipeEntity)
                .collect(Collectors.toList());

        List<RecipeEntity> dinnerEntities = dinnerResults.stream()
                .map(this::saveOrGetRecipeEntity)
                .collect(Collectors.toList());

        for (int i = 0; i < days; i++) {
            MealDay day = new MealDay();
            day.setBreakfast(breakfastEntities.get(i));
            day.setLunch(lunchEntities.get(i));
            day.setDinner(dinnerEntities.get(i));
            day.setMealPlan(mealPlan);
            day.setDay(getDayName(i));
            daysList.add(day);
        }

        mealPlan.setDays(daysList);
        return mealPlanRepository.save(mealPlan);
    }

    private String getDayName(int i) {
        switch (i) {
            case 0:
                return "Sunday";
            case 1:
                return "Monday";
            case 2:
                return "Tuesday";
            case 3:
                return "Wednesday";
            case 4:
                return "Thursday";
            case 5:
                return "Friday";
            case 6:
                return "Saturday";
            default:
                return "Unknown";
        }
    }

    private RecipeEntity saveOrGetRecipeEntity(RecipeResult r) {
        return recipeRepository.findById(r.getId())
                .orElseGet(() -> recipeRepository.save(RecipeEntity.fromRecipeResult(r)));
    }

    public RecipeEntity getAlternativeMeal(Long recipeId) {
        RecipeEntity original = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Recipe not found"));

        List<String> dishTypes = original.getDishTypes();
        if (dishTypes == null) {
            dishTypes = new ArrayList<String>();
        }

        List<RecipeEntity> sameDishTypes = recipeRepository.findByDishTypes(dishTypes, recipeId.longValue());
        if (!sameDishTypes.isEmpty()) {
            return sameDishTypes.get(0);
        }

        List<String> ingredientNames = original.getIngredients() == null
                ? new ArrayList<String>()
                : original.getIngredients().stream()
                        .map(IngredientInput::getName)
                        .collect(Collectors.toList());

        if (!ingredientNames.isEmpty()) {
            List<RecipeEntity> sharedIngredients = recipeRepository.findByIngredients(ingredientNames, recipeId.longValue());
            if (!sharedIngredients.isEmpty()) {
                return sharedIngredients.get(0);
            }
        }

        List<RecipeEntity> all = recipeRepository.findAll();
        for (RecipeEntity r : all) {
            if (r.getId() != recipeId.longValue()) {
                return r;
            }
        }

        throw new RuntimeException("No alternative recipe available");
    }

    @Transactional
    public RecipeEntity replaceMealInDay(Long dayId, String mealType) {
        MealDay day = mealDayRepository.findById(dayId)
                .orElseThrow(() -> new RuntimeException("MealDay not found"));

        RecipeEntity current = null;
        if ("breakfast".equalsIgnoreCase(mealType)) {
            current = day.getBreakfast();
        } else if ("lunch".equalsIgnoreCase(mealType)) {
            current = day.getLunch();
        } else if ("dinner".equalsIgnoreCase(mealType)) {
            current = day.getDinner();
        }

        RecipeEntity replacement;
        if (current != null) {
            replacement = getAlternativeMeal(current.getId());
        } else {
            List<RecipeEntity> all = recipeRepository.findAll();
            if (all.isEmpty()) {
                throw new RuntimeException("No recipes available");
            }
            replacement = all.get(0);
        }

        if ("breakfast".equalsIgnoreCase(mealType)) {
            day.setBreakfast(replacement);
        } else if ("lunch".equalsIgnoreCase(mealType)) {
            day.setLunch(replacement);
        } else if ("dinner".equalsIgnoreCase(mealType)) {
            day.setDinner(replacement);
        }

        mealDayRepository.save(day);
        return replacement;
    }
}
