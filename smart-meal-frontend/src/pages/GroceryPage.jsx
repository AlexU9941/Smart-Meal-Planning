import React, { useState } from "react";
import ConnectKroger from "./ConnectKroger";
import SearchProducts from "./SearchProducts";

const GroceryPage = () => {
  const [connected, setConnected] = useState(false);

  return (
    <div style={{ padding: "20px" }}>
      <h2>Smart Meal Planner - Kroger Grocery</h2>
      <ConnectKroger onConnected={() => setConnected(true)} />

      {connected && (
        <div style={{ marginTop: "30px" }}>
          <SearchProducts />
        </div>
      )}
    </div>
  );
};

export default GroceryPage;
