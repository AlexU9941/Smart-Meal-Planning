import React, { useState } from "react";

export default function CreateAccountForm({ onSignUp, switchToSignIn }) {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const handleSubmit = (e) => {
    e.preventDefault();
    const success = true;

    if (success) {
      onSignUp(); 
      alert("Account creation failed");
    }
  };

  return (
    <div>
      <h2>Create Account</h2>
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
        <button type="submit">Create Account</button>
      </form>
      <p>
        Already have an account?{" "}
        <button onClick={switchToSignIn}>Sign In</button>
      </p>
    </div>
  );
}
