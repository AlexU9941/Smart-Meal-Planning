import React, { useState } from "react";

export default function UpdateHealthInfo({ apiBase = "" }) {
    const [form, setForm] = useState({
        height: "",
        weight: "",
        age: "",
        sex: "",
        activityLevel: "",
        allergies: "",
        username: ""
    });
    const [status, setStatus] = useState({ loading: false, error: "", success: "" });

    const onChange = (e) => setForm(prev => ({ ...prev, [e.target.name]: e.target.value }));

    const onSubmit = async (e) => {
        e.preventDefault();
        setStatus({ loading: true, error: "", success: "" });
        if (!form.username) {
            setStatus({ loading: false, error: "Username is required to update.", success: "" });
            return;
        }
        try {
            const url = `${apiBase}/users/${encodeURIComponent(form.username)}/health`;
            const res = await fetch(url, {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    height: form.height,
                    weight: form.weight,
                    activityLevel: form.activityLevel,
                    allergies: form.allergies
                })
            });
            if (!res.ok) {
                const text = await res.text();
                throw new Error(text || res.statusText);
            }
            setStatus({ loading: false, error: "", success: "Updated successfully." });
        } catch (err) {
            setStatus({ loading: false, error: err.message || "Update failed.", success: "" });
        }
    };

    const rowStyle = { display: "flex", gap: "8px", marginBottom: "6px", alignItems: "center" };
    const inputStyle = { padding: "4px", minWidth: "120px" };
    const grow = { flex: 1 };

    return (
        <form onSubmit={onSubmit} style={{ maxWidth: 900 }}>
            <h3>Update Personal Health Info</h3>

            <div style={rowStyle}>
                <input name="height" placeholder="Height" value={form.height} onChange={onChange} style={inputStyle} />
                <input name="weight" placeholder="Weight" value={form.weight} onChange={onChange} style={inputStyle} />
            </div>

            <div style={rowStyle}>
                <select name="activityLevel" value={form.activityLevel} onChange={onChange} style={inputStyle}>
                    <option value="">Select activity level</option>
                    <option value="sedentary">Sedentary</option>
                    <option value="light">Light</option>
                    <option value="moderate">Moderate</option>
                    <option value="active">Active</option>
                </select>
                <input name="allergies" placeholder="Allergies" value={form.allergies} onChange={onChange} style={inputStyle} />
            </div>

            <div style={rowStyle}>
                <input
                    name="username"
                    placeholder="Username (required)"
                    value={form.username}
                    onChange={onChange}
                    style={{ ...inputStyle, ...grow }}
                />
                <div style={{ marginLeft: "auto" }}>
                    <button type="submit" disabled={status.loading}>
                        {status.loading ? "Updating..." : "Update"}
                    </button>
                </div>
            </div>

            {status.error && <div style={{ color: "red" }}>{status.error}</div>}
            {status.success && <div style={{ color: "green" }}>{status.success}</div>}
        </form>
    );
}