import React, { useState } from "react";

const SearchProducts = () => {
  const [term, setTerm] = useState("");
  const [results, setResults] = useState([]);
  const [loading, setLoading] = useState(false);

  const search = async () => {
    if (!term) return;
    setLoading(true);
    setResults([]);

    try {
      const res = await fetch(`/api/kroger/search?q=${encodeURIComponent(term)}`);
      
      if (!res.ok) {
        throw new Error(`Server error: ${res.status}`);
      }

      const data = await res.json();
      setResults(data);
    } catch (err) {
      console.error("Error fetching Kroger products:", err);
      alert("Failed to search products. Check console for details.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <h2>Search Grocery Products</h2>

      <input
        type="text"
        placeholder="Search ingredient"
        value={term}
        onChange={(e) => setTerm(e.target.value)}
        className="input-field"
      />
      <button onClick={search} className="button-primary">
        {loading ? "Searching..." : "Search"}
      </button>

      <ul>
        {results.map((item) => (
          <li key={item.productId}>
            <strong>{item.name}</strong>
            {item.imageUrl && (
              <img
                src={item.imageUrl}
                alt={item.name}
                style={{ width: "50px", marginLeft: "10px" }}
              />
            )}
          </li>
        ))}
      </ul>
    </div>
  );
};

export default SearchProducts;


/*import React, { useState } from "react";

const SearchProducts = () => {
  const [term, setTerm] = useState("");
  const [results, setResults] = useState([]);

  const search = async () => {
    if (!term) return;
    setLoading(true);
    setResults([]);

    try {
      const res = await fetch(`/api/kroger/search?q=${encodeURIComponent(term)}`);
      
      if (!res.ok) {
        throw new Error(`Server error: ${res.status}`);
      }

      const data = await res.json();
      setResults(data);
    } catch (err) {
      console.error("Error fetching Kroger products:", err);
      alert("Failed to search products. Check console for details.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <h2>Search Grocery Products</h2>

      <input
        type="text"
        placeholder="Search ingredient"
        value={term}
        onChange={(e) => setTerm(e.target.value)}
        className="input-field"
      />
      <button onClick={search} className="button-primary">
        {loading ? "Searching..." : "Search"}
      </button>

      <ul>
        {results.map((item) => (
          <li key={item.productId}>
            <strong>{item.name}</strong>
            {item.imageUrl && (
              <img
                src={item.imageUrl}
                alt={item.name}
                style={{ width: "50px", marginLeft: "10px" }}
              />
            )}
          </li>
        ))}
      </ul>
    </div>
  );
};

export default SearchProducts;
*/