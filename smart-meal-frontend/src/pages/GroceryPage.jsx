import React, { useState, useEffect } from "react";
import ConnectKroger from "./ConnectKroger";
import SearchProducts from "./SearchProducts";

const GroceryPage = () => {
  const [connected, setConnected] = useState(false);
  const [ingredients, setIngredients] = useState([]);

  // Load ingredients from localStorage initially
  useEffect(() => {
    const loadIngredients = () => {
      const saved = localStorage.getItem("mealPlanIngredients");
      setIngredients(saved ? JSON.parse(saved) : []);
    };

    loadIngredients();

    // Listen for changes to localStorage in case meal plan was updated in another tab
    const handleStorage = (event) => {
      if (event.key === "mealPlanIngredients") {
        loadIngredients();
      }
    };

    window.addEventListener("storage", handleStorage);

    // Custom event for same-tab updates
    const handleMealPlanUpdated = () => loadIngredients();
    window.addEventListener("mealPlanUpdated", handleMealPlanUpdated);

    return () => {
      window.removeEventListener("storage", handleStorage);
      window.removeEventListener("mealPlanUpdated", handleMealPlanUpdated);
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


/*import React, { useState } from "react";
import ConnectKroger from "./ConnectKroger";
import SearchProducts from "./SearchProducts";

const GroceryPage = () => {
  const [connected, setConnected] = useState(false);

  return (
    <div style={{ padding: "20px" }}>
      {Step 1: Kroger OAuth Connect}
      <ConnectKroger onConnected={() => setConnected(true)} />

      {Step 2: Search products AFTER connection}
      {connected && (
        <div style={{ marginTop: "30px" }}>
          <SearchProducts />
        </div>
      )}
    </div>
  );
};

export default GroceryPage;
*/

