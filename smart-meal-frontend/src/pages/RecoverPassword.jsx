import React, { useState } from 'react';
import axios from 'axios';

const RecoverPassword = () => {
  const [email, setEmail] = useState('');
  const [username, setUsername] = useState('');
  const [message, setMessage] = useState('');

  const handleRecover = async () => {
    try {
      const response = await axios.post('http://localhost:8080/api/recover-password', { email, username });
      setMessage(response.data.message);
    } catch (error) {
      setMessage(error.response?.data?.error || 'Connection failed. Try again later.');
    }
  };

  return (
    <div className="recover-password">
      <h2>Recover Password</h2>
      <input type="text" placeholder="Username" value={username} onChange={e => setUsername(e.target.value)} />
      <input type="email" placeholder="Email" value={email} onChange={e => setEmail(e.target.value)} />
      <button onClick={handleRecover}>Recover Password</button>
      {message && <p>{message}</p>}
    </div>
  );
};

export default RecoverPassword;
