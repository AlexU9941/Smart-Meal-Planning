import React, { useState } from "react";

export default function GrocerySearch({ provider, accessToken, onAdd }) {
    const [query, setQuery] = useState("");
    const [results, setResults] = useState([]);

    async function handleSearch() {
        const response = await fetch(
            `http://localhost:8080/api/grocery/search/${provider}?query=${query}&token=${accessToken}`
        );
        const data = await response.json();
        setResults(data);
    }

    return (
        <div>
            <h3>Search for items (@{provider})</h3>
            <input 
                value={query} 
                onChange={(e) => setQuery(e.target.value)} 
                placeholder="Search bananas, milk..."
            />
            <button onClick={handleSearch}>Search</button>

            <div style={{ marginTop: "20px" }}>
                {results.map(item => (
                    <div 
                        key={item.productId}
                        style={{
                            padding: "10px",
                            border: "1px solid #ccc",
                            marginBottom: "10px"
                        }}
                    >
                        <strong>{item.name}</strong>
                        <p>{item.brand}</p>
                        <p>${item.price}</p>
                        <button onClick={() => onAdd(item)}>
                            Add to Cart
                        </button>
                    </div>
                ))}
            </div>
        </div>
    );
}