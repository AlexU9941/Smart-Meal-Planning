
import { useState } from 'react';
import axios from 'axios';

export default function HealthInfoForm() {
  const [formData, setFormData] = useState({
    height: '', weight: '', age: '', sex: '', activity: '', allergies: ''
  });
  const [message, setMessage] = useState('');

  const handleChange = e => setFormData({ ...formData, [e.target.name]: e.target.value });

  const handleSubmit = async e => {
    e.preventDefault();
    try {
      await axios.post('/api/health-info', formData);
      setMessage('Health information saved.');
    } catch (err) {
      setMessage('Database connection failed.');
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <input name="height" placeholder="Height" onChange={handleChange} />
      <input name="weight" placeholder="Weight" onChange={handleChange} />
      <input name="age" placeholder="Age" onChange={handleChange} />
      <input name="sex" placeholder="Sex" onChange={handleChange} />
      <select name="activity" onChange={handleChange}>
        <option value="">Select activity level</option>
        <option value="light">Light</option>
        <option value="moderate">Moderate</option>
        <option value="high">High</option>
      </select>
      <input name="allergies" placeholder="Allergies" onChange={handleChange} />
      <button type="submit">Submit</button>
      <p>{message}</p>
    </form>
  );
}
