package smart_meal_planner.recipe;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RecipeSearchResponse {
    private List<RecipeResult> results; 

    public List<RecipeResult> getResults() {
        return results;
    }

    
    public void setResults(List<RecipeResult> results) {
        this.results = results;
    }

}
