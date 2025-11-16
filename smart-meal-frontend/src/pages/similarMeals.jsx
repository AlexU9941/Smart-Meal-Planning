import React, { useState } from 'react';

function SimilarMeals({ favorites }) {
  const [recommendations, setRecommendations] = useState([]);
  const [error, setError] = useState('');

  const findSimilarMeals = async () => {
    if (favorites.length === 0) {
      setError('No favorites selected.');
      return;
    }
    try {
      // Simulate API call
      const similarMeals = [
        { name: 'Grilled Chicken Salad', id: 1 },
        { name: 'Quinoa Veggie Bowl', id: 2 }
      ];
      setRecommendations(similarMeals);
    } catch {
      setError('Unable to fetch similar meals.');
    }
  };

  return (
    <div>
      <h3>Find Similar Meals</h3>
      <button onClick={findSimilarMeals}>Find</button>
      {error && <p className="error">{error}</p>}
      <ul>
        {recommendations.map(meal => (
          <li key={meal.id}>{meal.name}</li>
        ))}
      </ul>
    </div>
  );
}

export default SimilarMeals;