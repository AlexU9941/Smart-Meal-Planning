import React, { useState, useEffect } from "react";

export default function KrogerGrocery({ mealPlanIngredients = [] }) {
  const [searchResults, setSearchResults] = useState([]);
  const [cart, setCart] = useState({});
  const [loading, setLoading] = useState(false);

  // Normalize ingredient names
  const normalizedIngredients = mealPlanIngredients.map(i => i.trim().toLowerCase());

  const handleSearch = async () => {
    if (!normalizedIngredients.length) return;
    setLoading(true);

    try {
      const queryParam = encodeURIComponent(normalizedIngredients.join(","));
      const res = await fetch(`http://localhost:8080/api/kroger/search?q=${queryParam}`);
      if (!res.ok) throw new Error(`HTTP error! status: ${res.status}`);

      const data = await res.json();
      console.log("Kroger search results:", data);

      // Filter results to match any ingredient (case-insensitive)
      const filteredData = data.filter(item =>
        normalizedIngredients.some(ing => item.name?.toLowerCase().includes(ing))
      );

      // Initialize cart quantities
      const initialCart = {};
      filteredData.forEach(item => { initialCart[item.id] = 1; });

      setSearchResults(filteredData);
      setCart(initialCart);
    } catch (err) {
      console.error("Kroger search failed:", err);
      setSearchResults([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    handleSearch();
  }, [normalizedIngredients]); // triggers whenever ingredients change

  const updateQuantity = (id, quantity) => {
    if (quantity < 1) return;
    setCart(prev => ({ ...prev, [id]: quantity }));
  };

  const handleCheckout = () => {
    if (!Object.keys(cart).length) {
      alert("Select at least one item to checkout.");
      return;
    }
    const params = Object.entries(cart)
      .map(([id, qty]) => `productIds=${id}&quantities=${qty}`)
      .join("&");
    const url = `http://localhost:8080/api/kroger/checkout?${params}`;
    window.open(url, "_blank");
  };

  const totalPrice = searchResults.reduce((sum, item) => {
    const qty = cart[item.id] || 0;
    return sum + (item.price || 0) * qty;
  }, 0);

  return (
    <div style={{ padding: "1rem" }}>
      <h2>Kroger Grocery - Meal Plan Ingredients</h2>
      {!normalizedIngredients.length && <p>No ingredients to search. Generate a meal plan first.</p>}

      <button onClick={handleSearch} disabled={loading || !normalizedIngredients.length}>
        {loading ? "Searching..." : "Search Kroger"}
      </button>

      <ul style={{ listStyle: "none", padding: 0, marginTop: "1rem" }}>
        {searchResults.map(item => (
          <li key={item.id} style={{ display: "flex", alignItems: "center", marginBottom: "0.5rem", border: "1px solid #ccc", padding: "0.5rem", borderRadius: "4px" }}>
            {item.image && <img src={item.image} alt={item.name} style={{ width: "60px", height: "60px", marginRight: "1rem" }} />}
            <div style={{ flex: 1 }}>
              <strong>{item.name}</strong> - {item.brand} - ${item.price}
            </div>
            <input type="number" min="1" value={cart[item.id] || 1} onChange={e => updateQuantity(item.id, parseInt(e.target.value))} style={{ width: "50px", marginRight: "0.5rem" }} />
          </li>
        ))}
      </ul>

      {searchResults.length > 0 && (
        <div style={{ marginTop: "1rem" }}>
          <p>Total Estimated Price: ${totalPrice.toFixed(2)}</p>
          <button onClick={handleCheckout}>Checkout Selected Items</button>
        </div>
      )}
    </div>
  );
}

/* import React, { useState, useEffect } from "react";

export default function KrogerGrocery({ mealPlanIngredients = [] }) {
  const [searchResults, setSearchResults] = useState([]);
  const [cart, setCart] = useState({});
  const [loading, setLoading] = useState(false);

  // Auto-search when meal plan ingredients are provided
  useEffect(() => {
    if (mealPlanIngredients.length > 0) handleSearch();
  }, [mealPlanIngredients]);

  const handleSearch = async () => {
    if (!mealPlanIngredients.length) return;
    setLoading(true);

    try {
      const query = mealPlanIngredients.map(encodeURIComponent).join("&q=");
      const res = await fetch(`http://localhost:8080/api/kroger/search?q=${query}`);
      if (!res.ok) throw new Error(`HTTP error! status: ${res.status}`);
      
      const data = await res.json();
      console.log("Raw Kroger data:", data);

      const filteredData = data.filter(item =>
        mealPlanIngredients.some(ing => item.name?.toLowerCase().includes(ing.toLowerCase()))
      );

      const initialCart = {};
      filteredData.forEach(item => {
        initialCart[item.id] = 1;
      });

      setSearchResults(filteredData);
      setCart(initialCart);
    } catch (err) {
      console.error("Kroger search failed:", err);
    } finally {
      setLoading(false);
    }
  };

  const updateQuantity = (id, quantity) => {
    if (quantity < 1) return; // prevent 0 or negative
    setCart(prev => ({ ...prev, [id]: quantity }));
  };

  const handleCheckout = () => {
    if (!Object.keys(cart).length) {
      alert("Select at least one item to checkout.");
      return;
    }
    const params = Object.entries(cart)
      .map(([id, qty]) => `productIds=${id}&quantities=${qty}`)
      .join("&");
    const url = `http://localhost:8080/api/kroger/checkout?${params}`;
    window.open(url, "_blank");
  };

  const totalPrice = searchResults.reduce((sum, item) => {
    const qty = cart[item.id] || 0;
    return sum + item.price * qty;
  }, 0);

  return (
    <div style={{ padding: "1rem" }}>
      <h2>Kroger Grocery - Meal Plan Ingredients</h2>
      <p>
        These are the items you need for your meal plan. Adjust quantities and
        checkout directly.
      </p>

      <button onClick={handleSearch} disabled={loading}>
        {loading ? "Searching..." : "Search Kroger"}
      </button>

      <ul style={{ listStyle: "none", padding: 0, marginTop: "1rem" }}>
        {searchResults.map((item) => (
          <li
            key={item.id}
            style={{
              display: "flex",
              alignItems: "center",
              marginBottom: "0.5rem",
              border: "1px solid #ccc",
              padding: "0.5rem",
              borderRadius: "4px",
            }}
          >
            {item.image && (
              <img
                src={item.image}
                alt={item.name}
                style={{ width: "60px", height: "60px", marginRight: "1rem" }}
              />
            )}
            <div style={{ flex: 1 }}>
              <strong>{item.name}</strong> - {item.brand} - ${item.price}
            </div>
            <input
              type="number"
              min="1"
              value={cart[item.id] || 1}
              onChange={(e) => updateQuantity(item.id, parseInt(e.target.value))}
              style={{ width: "50px", marginRight: "0.5rem" }}
            />
          </li>
        ))}
      </ul>
      {searchResults.length > 0 && (
        <div style={{ marginTop: "1rem" }}>
          <p>Total Estimated Price: ${totalPrice.toFixed(2)}</p>
          <button onClick={handleCheckout}>Checkout Selected Items</button>
        </div>
      )}
    </div>
  );
}
*/