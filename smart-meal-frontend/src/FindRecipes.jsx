import React, { useState } from 'react';
import axios from 'axios';

const FindRecipes = () => {
  const [name, setName] = useState('');
  const [budget, setBudget] = useState('');
  const [results, setResults] = useState([]);
  const [message, setMessage] = useState('');

  const handleSearch = async () => {
    if (isNaN(budget) && budget !== '') {
      setMessage('Invalid budget input. Please enter a number.');
      return;
    }

    try {
      const response = await axios.get('/api/recipes/search', {
        params: { name, budget }
      });

      if (response.data.length === 0) {
        setMessage('No recipes match your search criteria.');
      } else {
        setResults(response.data);
        setMessage('');
      }
    } catch (error) {
      setMessage('Search failed. Please try again.');
    }
  };

  return (
    <div className="find-recipes">
      <h2>Find Recipes</h2>
      <input type="text" placeholder="Recipe Name" value={name} onChange={e => setName(e.target.value)} />
      <input type="text" placeholder="Estimated Budget" value={budget} onChange={e => setBudget(e.target.value)} />
      <button onClick={handleSearch}>Search</button>
      {message && <p>{message}</p>}
      <div className="results">
        {results.map(recipe => (
          <div key={recipe.id}>
            <h4>{recipe.name}</h4>
            <p>Budget: ${recipe.budget}</p>
          </div>
        ))}
      </div>
    </div>
  );
};

export default FindRecipes;
