import React, { useState } from "react";
import axios from "axios";
import "../css/signIn.css";

export default function SignInForm({ onSignIn, switchToCreate, switchToRecover }) {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  const handleSubmit = async (e) => {
  e.preventDefault();

  try {
    const response = await axios.post("http://localhost:8080/api/sign-in", {
      username: username,    // If backend expects "username"
      password: password,
    });

    console.log("Login successful:", response.data);
    onSignIn();  // Call parent callback to update UI
  } catch (error) {
    if (error.response && error.response.status === 401) {
      alert("Invalid username or password");
    } else {
      alert("Server error. Please try again.");
    }
  }
};
  

  return (
    <><div>
      <h3>Please sign in to use Smart Meal Planner!</h3>
      <form onSubmit={handleSubmit}>
        <input
          type="username"
          placeholder="Username"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          required />
        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required />
        <button type="submit">Sign In</button>
      </form>
      <p>
        Need to sign up?{" "}
        <button onClick={switchToCreate}>Create Account</button>
      </p>
    </div>
    <p>
        Forgot your password?{" "}
        <button onClick={switchToRecover}>
          Recover Password
        </button>
      </p>
    </>
  );
}
