import React, { useState } from "react";
import ConnectKroger from "./ConnectKroger";
import SearchProducts from "./SearchProducts";

const GroceryPage = () => {
  const [connected, setConnected] = useState(false);

  return (
    <div style={{ padding: "20px" }}>
      <h1>Grocery Store Integration</h1>

      {/* Step 1: Kroger OAuth Connect */}
      <ConnectKroger onConnected={() => setConnected(true)} />

      {/* Step 2: Search products AFTER connection */}
      {connected && (
        <div style={{ marginTop: "30px" }}>
          <SearchProducts />
        </div>
      )}
    </div>
  );
};

export default GroceryPage;
