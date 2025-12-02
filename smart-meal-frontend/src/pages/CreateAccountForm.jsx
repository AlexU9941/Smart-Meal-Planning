import React, { useState } from "react";
import axios from "axios";

export default function CreateAccountForm({ onSignUp, switchToSignIn }) {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [username, setUsername] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();
  
    try {
      const response = await axios.post("http://localhost:8080/api/create-account", {
        email,
        username,
        password,
      });

      console.log("Account created:", response.data);
      onSignUp();  
      alert("Account created successfully!");
    } 
    catch (error) {
      if (error.response && error.response.status === 409) {
        alert("Username or email already exists.");
      } else if (error.response && error.response.status === 400) {
        alert("Invalid input. Please fill all fields.");
      } else {
        alert("Server error. Please try again.");
      }
    }
  };

  return (
    <div>
      <h2>Create Account</h2>
      <form onSubmit={handleSubmit}>
         <input
          type="username"
          placeholder="Username"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          required
        />
        <input
          type="email"
          placeholder="Email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />
        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />
        <button type="submit">Create Account</button>
      </form>
      <p>
        Already have an account?{" "}
        <button onClick={switchToSignIn}>Sign In</button>
      </p>
    </div>
  );
}
