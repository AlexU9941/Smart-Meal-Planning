import React from "react";
import { useState } from 'react'; 
import axios from 'axios';  
export default function CreateAccountForm({ onSignUp }) {
  const [email, setEmail] = React.useState("");
  const [password, setPassword] = React.useState("");

  const handleSubmit = (e) => {
    e.preventDefault();

    // TODO: Create account logic here
    const success = true; // replace with actual signup logic

    if (success) {
      onSignUp(); // <- call callback to mark user as signed in
    } else {
      alert("Account creation failed");
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <h2>Create Account</h2>
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
  );
}

export default CreateAccountForm;

/*import { useState } from 'react';
import axios from 'axios';

export default function CreateAccountForm() {
  const [formData, setFormData] = useState({ username: '', email: '', password: '' });
  const [message, setMessage] = useState('');

  const handleChange = e => setFormData({ ...formData, [e.target.name]: e.target.value });

  const handleSubmit = async e => {
    e.preventDefault();
    const { username, email, password } = formData;

    if (!username || !email || !password) {
      setMessage('All fields are required.');
      return;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
      setMessage('Invalid email format.');
      return;
    }

    try {
      const res = await axios.post('http://localhost:8080/api/create-account', formData);
      setMessage('Account created successfully!');
      // Redirect to login
    } catch (err) {
      if (err.response?.status === 409) {
        setMessage('Email already exists.');
      } else {
        setMessage('Database connection failed.');
      }
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <input name="username" placeholder="Username" onChange={handleChange} />
      <input name="email" placeholder="Email" onChange={handleChange} />
      <input name="password" type="password" placeholder="Password" onChange={handleChange} />
      <button type="submit">Create Account</button>
      <p>{message}</p>
    </form>
  );
}
*/