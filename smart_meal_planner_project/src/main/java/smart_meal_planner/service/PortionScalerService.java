package smart_meal_planner.service;

import org.springframework.stereotype.Service;
import smart_meal_planner.dto.Ingredient;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class PortionScalerService {

    private final Connection conn;

    public PortionScalerService() throws SQLException {
        // Replace with your DB config
        conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/mealplanner", "user", "pass");
    }

    public List<Ingredient> scaleRecipe(int recipeId, int desiredServings) throws SQLException {
        List<Ingredient> scaledIngredients = new ArrayList<>();
        String query = "SELECT name, quantity, unit, servings FROM ingredients WHERE recipe_id = ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, recipeId);
        ResultSet rs = ps.executeQuery();

        int originalServings = 1;

        while (rs.next()) {
            String name = rs.getString("name");
            double quantity = rs.getDouble("quantity");
            String unit = rs.getString("unit");
            originalServings = rs.getInt("servings");

            double scaledQuantity = quantity * ((double) desiredServings / originalServings);
            scaledIngredients.add(new Ingredient(name, scaledQuantity, unit));
        }

        return scaledIngredients;
    }
}
