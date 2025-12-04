import React, { useState, useEffect, useCallback } from "react";

export default function SearchProducts({ ingredients = [], autoSearch = false }) {
  const [searchResults, setSearchResults] = useState([]);
  const [cart, setCart] = useState({});
  const [loading, setLoading] = useState(false);

  // =========================
  // Handle Kroger search
  // =========================
  const handleSearch = useCallback(async () => {
    console.log("Starting Kroger search for ingredients:", ingredients);
    if (!ingredients.length) return;

    setLoading(true);
    setSearchResults([]);

    try {
      let allResults = [];

      for (const ingredient of ingredients) {
        console.log("Searching Kroger for ingredient:", ingredient);
        const query = encodeURIComponent(ingredient);
        const res = await fetch(`http://localhost:8080/api/kroger/search?q=${query}`, {
          credentials: "include",
        });

        if (!res.ok) {
          console.error("HTTP error for ingredient:", ingredient, "status:", res.status);
          continue;
        }

        const data = await res.json();
        console.log("Results for", ingredient, ":", data);
        allResults.push(...data);
      }

      // Deduplicate products by ID
      const unique = Object.values(
        allResults.reduce((acc, item) => {
          acc[item.id] = item;
          return acc;
        }, {})
      );

      console.log("Unique products after dedupe:", unique);

      // Setup default quantity = 1
      const initialCart = {};
      unique.forEach((product) => {
        initialCart[product.id] = 1;
      });

      setSearchResults(unique);
      setCart(initialCart);
      console.log("SearchResults state updated. Cart state initialized.");

    } catch (err) {
      console.error("Kroger search error:", err);
      alert("Error searching Kroger products. See console for details.");
    }

    setLoading(false);
  }, [ingredients]);

  // =========================
  // Auto search on mount or ingredient change
  // =========================
  useEffect(() => {
    console.log("useEffect triggered. autoSearch:", autoSearch, "ingredients:", ingredients);
    if (autoSearch && ingredients.length > 0) {
      handleSearch();
    }
  }, [autoSearch, ingredients, handleSearch]);

  // =========================
  // Cart handling
  // =========================
  const updateQty = (id, qty) => {
    if (qty < 1) return;
    setCart((prev) => ({ ...prev, [id]: qty }));
  };

  const handleCheckout = () => {
    const params = Object.entries(cart)
      .map(([id, qty]) => `productIds=${id}&quantities=${qty}`)
      .join("&");

    console.log("Opening checkout with params:", params);
    window.open(`http://localhost:8080/api/kroger/checkout?${params}`, "_blank");
  };

  // =========================
  // Render
  // =========================
  return (
    <div>
      <button onClick={handleSearch} disabled={loading}>
        {loading ? "Searching…" : "Re-Search"}
      </button>

      <ul style={{ listStyle: "none", padding: 0 }}>
        {searchResults.map((item) => (
          <li
            key={item.id}
            style={{
              margin: "10px 0",
              padding: "10px",
              border: "1px solid #ccc",
              borderRadius: "6px",
              display: "flex",
              alignItems: "center",
            }}
          >
            {item.image && (
              <img
                src={item.image}
                alt={item.name}
                style={{ width: "60px", marginRight: "15px" }}
              />
            )}
            <div style={{ flex: 1 }}>
              <strong>{item.name}</strong>
              <div>{item.brand}</div>
              <div>${item.price}</div>
            </div>

            <input
              type="number"
              min="1"
              value={cart[item.id] || 1}
              onChange={(e) => updateQty(item.id, parseInt(e.target.value))}
              style={{ width: "60px", marginRight: "10px" }}
            />
          </li>
        ))}
      </ul>

      {searchResults.length > 0 && (
        <button onClick={handleCheckout} style={{ marginTop: "20px" }}>
          Checkout Selected Items
        </button>
      )}
    </div>
  );
}
