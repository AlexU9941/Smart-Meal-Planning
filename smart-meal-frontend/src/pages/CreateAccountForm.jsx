import React, { useState } from "react";

export default function CreateAccountForm({ onSignUp, switchToSignIn }) {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const handleSubmit = (e) => {
    e.preventDefault();
    const success = true;

    if (success) {
      onSignUp(); 
    } else {
      alert("Account creation failed");
    }
  };

  return (
    <div>
      <h2 style={{ display: 'none' }}>Create Account</h2>
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
          <button type="submit">Create Account</button>
          <button type="button" className="secondary" onClick={switchToSignIn}>Sign In</button>
        </div>
      </form>
    </div>
  );
}
