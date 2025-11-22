import React from "react";

export default function ProviderSelector({ onSelect }) {
    return (
        <div style={{ display: "flex", gap: "1rem", marginBottom: "20px" }}>
            <button onClick={() => onSelect("KROGER")}>Kroger</button>
        </div>
    );
}
