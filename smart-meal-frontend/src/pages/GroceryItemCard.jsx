import React from "react";

export default function GroceryItemCard({ item }) {
    return (
        <div style={{ border: "1px solid gray", padding: "10px", margin: "10px" }}>
            <h4>{item.name}</h4>
            <p>{item.brand}</p>
            <p>Price: ${item.price}</p>
        </div>
    );
}
