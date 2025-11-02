
import { useState } from 'react';
import axios from 'axios';

export default function HealthInfoForm() {
  const [formData, setFormData] = useState({
    heightFt: '', heightIn: '', weight: '', sex: '', activity: '', allergies: ''
  });
  const [message, setMessage] = useState('');

  const handleChange = e => setFormData({ ...formData, [e.target.name]: e.target.value });

  // const handleSubmit = async e => {
  //   e.preventDefault();
  //   try {
  //     await axios.post('http://localhost:8080/api/health-info', formData);
  //     setMessage('Health information saved.');
  //   } catch (err) {
  //     console.error(err);
  //     setMessage('Database connection failed.');
  //   }
  // };
  const handleSubmit = async e => {
    e.preventDefault();

    try {
      // Split allergies by comma if non-empty, else send empty array
      const allergiesList = formData.allergies
        ? formData.allergies.split(',').map(a => a.trim())
        : [];

      //const uid = localStorage.getItem('uid');
      const email = localStorage.getItem("email") 
      await axios.post('http://localhost:8080/api/health-info', {
        ...formData,
        allergies: allergiesList,
        email
      });

      setMessage('Health information saved.');
    } catch (err) {
      console.error(err);
      setMessage('Failed to save health information.');
    }
};

  return (
    <form onSubmit={handleSubmit}>
      <input name="heightFt" placeholder="Height (Feet)" onChange={handleChange} />
      <input name="heightIn" placeholder="Height (Inches)" onChange={handleChange} />
      <input name="weight" placeholder="Weight (lbs)" onChange={handleChange} />
      <input name="dob" type="date" placeholder="Date of Birth" />
      <select name="sex" onChange={handleChange}>
        <option value="">Select sex</option>
        <option value="male">male</option>
        <option value="female">female</option>
        <option value="other">prefer not to say</option>
      </select>
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
