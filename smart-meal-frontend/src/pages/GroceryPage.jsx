import React, { useState } from "react";
import ProviderSelector from "./ProviderSelector";
import GrocerySearch from "./GrocerySearch";

export default function GroceryPage() {
    const [provider, setProvider] = useState(null);
    const [accessToken, setAccessToken] = useState(null);
    const [cart, setCart] = useState([]);

    async function startProviderAuth(provider) {
        const url = `http://localhost:8080/api/grocery/auth/${provider}`;
        window.location.href = url; // redirect to Kroger OAuth
    }

    function addItem(item) {
        setCart(prev => [...prev, item]);
    }

    async function checkout() {
        const res = await fetch(
            `http://localhost:8080/api/grocery/checkout/${provider}`, 
            {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    items: cart.map(i => i.productId),
                    token: accessToken
                })
            }
        );

        const data = await res.text(); // checkout redirect URL
        window.location.href = data;
    }

    return (
        <div style={{ padding: "20px" }}>
            <h2>Grocery Integration</h2>

            <ProviderSelector 
                onSelect={(prov) => {
                    setProvider(prov);
                    startProviderAuth(prov);
                }} 
            />

            {provider && accessToken && (
                <GrocerySearch 
                    provider={provider}
                    accessToken={accessToken}
                    onAdd={addItem}
                />
            )}

            {cart.length > 0 && (
                <div>
                    <h3>Your Cart</h3>
                    {cart.map(item => (
                        <p key={item.productId}>{item.name}</p>
                    ))}

                    <button onClick={checkout}>Checkout</button>
                </div>
            )}
        </div>
    );
}
