import React, { useEffect, useState } from 'react';
import axios from 'axios';

const Favorites = () => {
  const [favorites, setFavorites] = useState([]);

  useEffect(() => {
    axios.get('/api/favorites')
      .then(res => setFavorites(res.data))
      .catch(() => alert('Failed to load favorites.'));
  }, []);

  return (
    <div className="favorites">
      <h2>Your Favorite Recipes</h2>
      {favorites.map(recipe => (
        <div key={recipe.id}>
          <h4>{recipe.name}</h4>
          <p>Budget: ${recipe.budget}</p>
        </div>
      ))}
    </div>
  );
};

export default Favorites;
