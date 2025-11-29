import React, { useEffect, useState } from 'react';
import axios from 'axios';

function SavedRecipes() {
  const [recipes, setRecipes] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchSavedRecipes = async () => {
      try {
        const response = await axios.get('/api/recipes/saved');
        setRecipes(response.data);
      } catch (err) {
        setError('Failed to load saved recipes.');
      }
    };
    fetchSavedRecipes();
  }, []);

  return (
    <div className="saved-recipes">
      <h2>Saved Recipes</h2>
      {error && <p className="error">{error}</p>}
      {recipes.length === 0 ? (
        <p>No saved recipes yet.</p>
      ) : (
        <div className="recipe-list">
          {recipes.map((recipe) => (
            <div key={recipe.id} className="recipe-card">
              <img src={recipe.image} alt={recipe.title} />
              <h3>{recipe.title}</h3>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default SavedRecipes;
