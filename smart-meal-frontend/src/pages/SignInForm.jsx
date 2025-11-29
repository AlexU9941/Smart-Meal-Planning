import React, { useState } from "react";
import axios from "axios";

export default function SignInForm({ onSignIn }) {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const res = await axios.post("http://localhost:8080/api/sign-in", { email, password });
      if (res.data.success) {
        onSignIn(); 
      } else {
        alert("Invalid credentials");
      }
    } catch (err) {
      console.error(err);
      alert("Sign in failed");
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <h2>Sign In</h2>
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
  );
}


/*import { useState } from 'react'; 
import axios from 'axios'; 


export default function SignInForm() {
  const [formData, setFormData] = useState({ username: '', password: '' });
  const [message, setMessage] = useState('');

  const handleChange = e => setFormData({ ...formData, [e.target.name]: e.target.value });

  const handleSubmit = async e => {
    e.preventDefault();
    const { username, password } = formData;

    if (!username || !password) {
      setMessage('All fields are required.');
      return;
    }

    try {
      const res = await axios.post('http://localhost:8080/api/sign-in', formData);
      const user = res.data;   // contains UID

      console.log("Response data:", user); // See exactly what comes back


      if (user.email) {
      localStorage.setItem('email', user.email);
      console.log("Saving email:", user.email);
      } else {
        console.warn("⚠️ No email returned from backend!");
      }

      setMessage('Signed in successfully!');
      // Redirect to home page
    } catch (err) {
      if (err.response?.status === 401) {
        setMessage('Incorrect username or password.');
      } else {
        setMessage('Database connection failed.');
      }
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <input name="username" placeholder="Username" onChange={handleChange} />
      <input name="password" type="password" placeholder="Password" onChange={handleChange} />
      <button type="submit">Sign In</button>
      <p>{message}</p>
    </form>
  );
}
*/