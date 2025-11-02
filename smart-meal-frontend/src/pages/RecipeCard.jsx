import React from 'react';
import axios from 'axios';

const RecipeCard = ({ recipe, onFavorite }) => {
  const handleFavorite = async () => {
    try {
      await axios.post('http://localhost:8080/api/favorites', { recipeId: recipe.id });
      onFavorite(recipe);
      alert('Recipe saved to favorites!');
    } catch (error) {
      alert('Failed to save recipe. Please try again.');
    }
  };

  return (
    <div className="recipe-card">
      <h3>{recipe.name}</h3>
      <p>Budget: ${recipe.budget}</p>
      <button onClick={handleFavorite}>❤️ Favorite</button>
    </div>
  );
};

export default RecipeCard;
