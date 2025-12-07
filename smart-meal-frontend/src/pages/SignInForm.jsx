import React, { useState } from "react";
import axios from "axios";

export default function SignInForm({ onSignIn, switchToCreate, switchToRecover }) {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const response = await axios.post(
        "http://localhost:8080/api/sign-in",
        { username, password },
        { withCredentials: true }
      );

      localStorage.setItem("user", JSON.stringify(response.data));
      console.log("Logged in user:", response.data);

      onSignIn();
    } catch (error) {
      alert("Invalid credentials or server error.");
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-card">
        <h2>Welcome Back</h2>
        <p style={{ color: '#666' }}>Sign in to continue to Smart Meal Planner</p>
        <form onSubmit={handleSubmit} className="auth-box">
          <input
            type="username"
            placeholder="Username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
          />
          <input
            type="password"
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
          <div style={{ display: 'flex', gap: 8, justifyContent: 'center', marginTop: 6 }}>
            <button type="submit">Sign In</button>
          </div>
        </form>
        <p style={{ marginTop: 10 }}>
          Don't have an account?{" "}
          <button className="secondary" onClick={switchToCreate}>Create Account</button>
        </p>
        <p>
          Forgot your password?{" "}
          <button className="ghost" onClick={switchToRecover}>
            Recover Password
          </button>
        </p>
      </div>
    </div>
  );
}
