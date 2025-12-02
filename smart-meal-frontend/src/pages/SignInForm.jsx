import React, { useState } from "react";
import axios from "axios";

export default function SignInForm({ onSignIn, switchToCreate }) {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const handleSubmit = (e) => {
    e.preventDefault();
    const success = true;

    if (success) {
      onSignIn(); 
    } else {
      alert("Sign in failed");
    }
  };

  return (
    <div>
      <h2>Sign In</h2>
      <form onSubmit={handleSubmit}>
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
        <button type="submit">Sign In</button>
      </form>
      <p>
        Don't have an account?{" "}
        <button onClick={switchToCreate}>Create Account</button>
      </p>
    </div>
  );
}
