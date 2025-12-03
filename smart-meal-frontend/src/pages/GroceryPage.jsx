import React, { useState, useEffect } from "react";
import ConnectKroger from "./ConnectKroger";
import SearchProducts from "./SearchProducts";

const GroceryPage = () => {
  const [connected, setConnected] = useState(false);
  const [ingredients, setIngredients] = useState([]);

  // Load meal plan ingredients from localStorage
  useEffect(() => {
    const loadIngredients = () => {
      const saved = localStorage.getItem("mealPlanIngredients");
      setIngredients(saved ? JSON.parse(saved) : []);
    };

    loadIngredients();

    // Update ingredients whenever meal plan updates
    window.addEventListener("storage", loadIngredients);
    window.addEventListener("mealPlanUpdated", loadIngredients);

    return () => {
      window.removeEventListener("storage", loadIngredients);
      window.removeEventListener("mealPlanUpdated", loadIngredients);
    };
  }, []);

  return (
    <div style={{ padding: "20px", fontFamily: "Arial, sans-serif" }}>
      <h1>Kroger Grocery List</h1>

      {/* DISPLAY INGREDIENTS */}
      {!ingredients.length ? (
        <p style={{ fontSize: "1.1rem" }}>
          No meal plan generated yet. Go to <strong>Generate Meal Plan</strong> first!
        </p>
      ) : (
        <div
          style={{
            background: "#eef7ff",
            padding: "15px",
            borderRadius: "8px",
            marginBottom: "20px",
          }}
        >
          <h3>Ingredients Needed:</h3>
          <ul>
            {ingredients.map((ing, idx) => (
              <li key={idx}>{ing}</li>
            ))}
          </ul>
        </div>
      )}

      {/* STEP 1: KROGER OAUTH */}
      <ConnectKroger onConnected={() => setConnected(true)} />

      {/* STEP 2: SEARCH KROGER AFTER CONNECTING */}
      {connected && ingredients.length > 0 && (
        <div style={{ marginTop: "25px" }}>
          <h3>Searching Kroger products for your ingredients…</h3>

          <SearchProducts ingredients={ingredients} autoSearch={true} />
        </div>
      )}
    </div>
  );
};

export default GroceryPage;
