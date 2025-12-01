import React, { useState } from "react";
import axios from "axios";

export default function SignInForm({ onSignIn, switchToCreate }) {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const response = await axios.post("http://localhost:8080/api/sign-in", {
        username,
        password
      });

      console.log("Signed in:", response.data);
      alert("Signed in as " + response.data.username);
      
      onSignIn();

    } catch (error) {
      alert("Login failed: " + (error.response?.data || "Unknown error"));
    }
  };

  return (
    <div className="form-container">
      <h2>Sign In</h2>

      <form onSubmit={handleSubmit}>
        <input
          type="text"
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

        <button type="submit" className="primary-button">Sign In</button>
      </form>

      <p>
        Don’t have an account?
        <button className="secondary-button" onClick={switchToCreate}>
          Create Account
        </button>
      </p>
    </div>
  );
}
