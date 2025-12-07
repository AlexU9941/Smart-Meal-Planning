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
    <div>
      <h2>Sign In</h2>
      <form onSubmit={handleSubmit}>
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

        <button type="submit">Sign In</button>
      </form>

      <p>
        Don't have an account?{" "}
        <button onClick={switchToCreate}>Create Account</button>
        <br />
        Forgot password?{" "}
        <button onClick={switchToRecover}>Recover</button>
      </p>
    </div>
  );
}
