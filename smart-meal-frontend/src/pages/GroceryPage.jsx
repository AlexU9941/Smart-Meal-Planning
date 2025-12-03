import React, { useState, useEffect } from "react";
import ConnectKroger from "./ConnectKroger";
import SearchProducts from "./SearchProducts";

const GroceryPage = () => {
  const [connected, setConnected] = useState(false);
  const [ingredients, setIngredients] = useState([]);

  // Load ingredients from localStorage when page loads
  useEffect(() => {
  const loadIngredients = () => {
    const saved = localStorage.getItem("mealPlanIngredients");
    if (saved) {
      setIngredients(JSON.parse(saved));
    } else {
      setIngredients([]);
    }
  };

  loadIngredients(); // first load
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

      {!ingredients.length ? (
        <p>
          No meal plan generated yet. Go to <strong>Generate Meal Plan</strong> first!
        </p>
      ) : (
        <div style={{ marginBottom: "20px", padding: "15px", background: "#f0f8ff", borderRadius: "8px" }}>
          <h3>Ingredients from your weekly meal plan:</h3>
          <p>{ingredients.join(", ")}</p>
        </div>
      )}

      <ConnectKroger onConnected={() => setConnected(true)} />

      {connected && ingredients.length > 0 && (
        <div style={{ marginTop: "30px" }}>
          <h3>Searching Kroger for your ingredients...</h3>
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