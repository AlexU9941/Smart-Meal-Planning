package smart_meal_planner.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PortionScaler {

    private Connection conn;

    public PortionScaler(Connection conn) {
        this.conn = conn;
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

    // Ingredient class
    public static class Ingredient {
        public String name;
        public double quantity;
        public String unit;

        public Ingredient(String name, double quantity, String unit) {
            this.name = name;
            this.quantity = quantity;
            this.unit = unit;
        }
    }
}
