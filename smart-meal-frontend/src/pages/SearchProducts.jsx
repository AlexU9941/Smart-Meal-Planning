import React, { useState } from "react";

const SearchProducts = () => {
  const [term, setTerm] = useState("");
  const [results, setResults] = useState([]);

  const search = async () => {
    const res = await fetch(`/api/kroger/search?q=${term}`);
    const data = await res.json();
    setResults(data);
  };

  return (
    <div>
      <h2>Search Grocery Prices</h2>

      <input
        placeholder="Search ingredient"
        value={term}
        onChange={(e) => setTerm(e.target.value)}
        className="input-field"
      />

      <button onClick={search} className="button-primary">
        Search
      </button>

      <ul>
        {results.map((item) => (
          <li key={item.productId}>
            <strong>{item.name}</strong> — ${item.price} ({item.size})
          </li>
        ))}
      </ul>
    </div>
  );
};

export default SearchProducts;
