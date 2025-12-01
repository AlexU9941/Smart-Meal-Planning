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
      <h2 style={{ display: 'none' }}>Sign In</h2>
      <form onSubmit={handleSubmit} className="auth-box">
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
        <div style={{ display: 'flex', gap: '0.5rem', justifyContent: 'center', marginTop: '0.5rem' }}>
          <button type="submit">Sign In</button>
          <button type="button" className="secondary" onClick={switchToCreate}>Create Account</button>
        </div>
      </form>
    </div>
  );
}
