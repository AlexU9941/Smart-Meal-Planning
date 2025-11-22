import axios from "axios";

const API = "http://localhost:8080/api/grocery";

export async function startAuth(provider) {
    const res = await axios.get(`${API}/auth/${provider}`);
    return res.data; // returns auth URL
}

export async function searchProducts(provider, query, accessToken) {
    const res = await axios.get(`${API}/search/${provider}`, {
        params: {
            q: query,
            token: accessToken
        }
    });
    return res.data; 
}

export async function buildCheckout(provider, items, accessToken) {
    const res = await axios.post(`${API}/checkout/${provider}`, {
        items,
        token: accessToken
    });
    return res.data; // returns redirect URL
}
